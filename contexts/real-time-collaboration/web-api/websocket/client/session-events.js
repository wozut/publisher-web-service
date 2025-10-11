// Global variables
let stompClient = null;
let isConnected = false;
let currentDocumentId = null;
let sessionStateSnapshot = null;
let myUserId = null;
let myWriterId = null;
let subscriptionToUpdates = null;
let subscriptionToSessionState = null;
let myCursorPosition = null;
let requestSequenceNumber = 0;
let sessionEvents = [];
let lastSessionEventSequenceNumber = null;
const textRemovedType = "TextRemoved";
const textAddedType = "TextAdded";
const cursorPositionChangedType = "CursorPositionChanged";


// Utility functions
function addMessage(message, type = 'info') {
    const messages = document.getElementById('messages');
    if (!messages) return; // Exit if element doesn't exist

    const messageDiv = document.createElement('div');
    const timestamp = new Date().toLocaleTimeString();
    messageDiv.innerHTML = `<span style="color: #666">${timestamp}</span> [${type.toUpperCase()}] ${message}`;
    messages.appendChild(messageDiv);
    messages.scrollTop = messages.scrollHeight;
}

function clearMessages() {
    document.getElementById('messages').innerHTML = '';
}

function updateEventsDisplay() {
    const eventsContainer = document.getElementById('eventsContainer');
    if (!eventsContainer) return; // Exit if element doesn't exist

    if (sessionEvents.length === 0) {
        eventsContainer.innerHTML = '<div style="color: #666; padding: 10px; text-align: center;">No events received yet</div>';
        return;
    }

    eventsContainer.innerHTML = sessionEvents.map(event => `
                <div class="event-item">
                    <div><strong>Type:</strong> ${event.type}</div>
                    <div><strong>Seq:</strong> ${event.sequenceNumber || 'N/A'}</div>
                    <div><strong>Writer:</strong> ${event.writerId || 'N/A'}</div>
                    ${event.position !== undefined ? `<div><strong>Position:</strong> ${event.position}</div>` : ''}
                    ${event.text ? `<div><strong>Text:</strong> "${event.text}"</div>` : ''}
                    ${event.length ? `<div><strong>Length:</strong> ${event.length}</div>` : ''}
                    ${event.newPosition !== undefined ? `<div><strong>New Position:</strong> ${event.newPosition}</div>` : ''}
                </div>
            `).join('');

    eventsContainer.scrollTop = eventsContainer.scrollHeight;
}

function clearEvents() {
    sessionEvents.length = 0;
    // Only update UI if we're in browser environment
    if (typeof window !== 'undefined' && typeof document !== 'undefined') {
        updateEventsDisplay();
    }
}

function eventIsRemote(event) {
    if (event.broadcasted !== undefined && event.broadcasted !== null) {
        return event.broadcasted;
    } else {
        throw new Error("event.broadcasted is undefined or null");
    }
}

function eventIsLocal(event) {
    return !eventIsRemote(event);
}

function findInsertionPosition(eventSequenceNumber) {
    let left = 0;
    let right = sessionEvents.length;

    while (left < right) {
        const mid = Math.floor((left + right) / 2);
        const midSequenceNumber = sessionEvents[mid].sequenceNumber;

        if (midSequenceNumber < eventSequenceNumber) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }
    return left;
}

function insertEvent(insertionPosition, event) {
    sessionEvents.splice(insertionPosition, 0, event);
}

function processSessionEvent(event) {
    /* TODO crear evento y pasar a processCollaborativeEvent
* processCollaborativeEvent decide todo. Es decir, si hay que enviar un mensaje o no, si hay que deshacer cambios o no, etc.
*/
    const isRemote = eventIsRemote(event);
    const isLocal = eventIsLocal(event);
    const eventSequenceNumber = event.sequenceNumber;

    if (isRemote && (eventSequenceNumber === undefined || eventSequenceNumber === null ||
        typeof eventSequenceNumber !== 'number' || eventSequenceNumber < 0)) {
        throw new Error('Event sequence number must be a number greater than or equal to 0');
    }

    if(isRemote) {
        let insertionPosition = findInsertionPosition(eventSequenceNumber);
        insertEvent(insertionPosition, event);

        // Si este es el primer evento remoto y aún no estamos suscritos al session-state, suscribirse ahora
        const hasRemoteEvents = sessionEvents.some(e => eventIsRemote(e));
        if (hasRemoteEvents && subscriptionToSessionState === null && stompClient && currentDocumentId) {
            subscriptionToSessionState = stompClient.subscribe(`/user/${myUserId}/queue/session-state/${currentDocumentId}`, function (message) {
                console.log(`Time: ${(new Date(Date.now())).toISOString()}. (/user/${myUserId}/queue/session-state/${currentDocumentId}) Message received: ${message.body}`);
                sessionStateSnapshot = JSON.parse(message.body);
                let myWriterState = sessionStateSnapshot.writerStates.find((writerState) => (writerState.userId === myUserId))
                if (myWriterState) {
                    myWriterId = myWriterState.writerId;
                }
                addMessage('Received session state update', 'success');
                setInitialDocumentContent();
                updateUI();
            });
            addMessage('First remote event received, subscribing to session-state', 'info');
        }

        const existingRemoteEvent = sessionEvents.some(e =>
            eventIsRemote(e) && e.sequenceNumber === eventSequenceNumber
        );

        if (existingRemoteEvent) {
            console.log(`Remote event with sequence number ${eventSequenceNumber} already exists, skipping insertion`);
            return;
        }
    }

    if(isRemote) return;

    if(isLocal) {

    }

    if(event.type === cursorPositionChangedType) {
        const request = {
            sessionId: sessionStateSnapshot.id,
            writerId: myWriterId,
            newPosition: event.newPosition,
            sequenceNumber: requestSequenceNumber++
        };

        stompClient.publish({
            destination: '/app/change-cursor-position',
            body: JSON.stringify(request)
        })
    }

    if(event.type === textAddedType) {
        const request = {
            sessionId: sessionStateSnapshot.id,
            writerId: myWriterId,
            position: event.position,
            text: event.text,
            sequenceNumber: requestSequenceNumber++
        };

        stompClient.publish({
            destination: '/app/add-text',
            body: JSON.stringify(request)
        })
    }


    if(event.type === textRemovedType) {
        const request = {
            sessionId: sessionStateSnapshot.id,
            writerId: myWriterId,
            position: event.position,
            length: event.length,
            sequenceNumber: requestSequenceNumber++
        };

        stompClient.publish({
            destination: '/app/remove-text',
            body: JSON.stringify(request)
        })
    }
}

function updateUI() {
    // Only update UI if we're in browser environment with actual DOM elements
    if (typeof window === 'undefined' || typeof document === 'undefined') {
        return;
    }

    // Check if required elements exist
    const connectBtn = document.getElementById('connectBtn');
    if (!connectBtn) return; // Exit if we're in a test environment

    const connected = isConnected;
    const joined = connected && sessionStateSnapshot !== null;

    connectBtn.disabled = connected;
    document.getElementById('disconnectBtn').disabled = !connected;
    document.getElementById('joinBtn').disabled = !connected || joined;

    const status = document.getElementById('status');
    if (status) {
        if (connected) {
            status.textContent = joined ? `Connected and joined to ${currentDocumentId}` : 'Connected';
            status.className = 'status connected';
        } else {
            status.textContent = 'Disconnected';
            status.className = 'status disconnected';
        }
    }

    updateSessionInfo();
    updateWritersList();
}

function updateSessionInfo() {
    const info = document.getElementById('sessionInfo');
    if (!info) return; // Exit if element doesn't exist

    if (sessionStateSnapshot) {
        info.innerHTML = `
                    <div><strong>Session ID:</strong> ${sessionStateSnapshot.id}</div>
                    <div><strong>Document ID:</strong> ${currentDocumentId}</div>
                    <div><strong>Document Length:</strong> ${sessionStateSnapshot.documentState.content.length} chars</div>
                    <div><strong>Sequence:</strong> ${lastSessionEventSequenceNumber}</div>
                `;
    } else {
        info.textContent = isConnected ? 'Connected but not joined' : 'Not connected';
    }
}

function updateWritersList() {
    const writersDiv = document.getElementById('writers');
    if (!writersDiv) return; // Exit if element doesn't exist

    if (sessionStateSnapshot && sessionStateSnapshot.writerStates) {
        const writers = sessionStateSnapshot.writerStates;
        if (writers.length > 0) {
            writersDiv.innerHTML = writers.map(writer => `
                        <div class="writer">
                            <strong>User:</strong> ${writer.userId}<br>
                            <strong>Cursor:</strong> ${writer.cursorPosition || 'N/A'}<br>
                            <strong>Selected:</strong> ${writer.selectedText ?
                `pos ${writer.selectedText.position}, len ${writer.selectedText.length}` : 'None'}
                        </div>
                    `).join('');
        } else {
            writersDiv.innerHTML = 'None';
        }
    } else {
        writersDiv.innerHTML = 'Not joined';
    }
}

function setInitialDocumentContent() {
    if (sessionStateSnapshot && sessionStateSnapshot.documentState) {
        const textarea = document.getElementById('documentContent');
        const currentCursor = textarea.selectionStart;
        textarea.value = sessionStateSnapshot.documentState.content;

        // Restore cursor if possible
        if (currentCursor <= textarea.value.length) {
            textarea.setSelectionRange(currentCursor, currentCursor);
        }
    }
}

// WebSocket connection management
function connect() {
    myUserId = document.getElementById('userId').value.trim();
    if (!myUserId) {
        alert('Please enter a User ID');
        return;
    }

    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/session',
        connectHeaders: {
            'UserId': myUserId
        },
        onConnect: function (frame) {
            isConnected = true;
            addMessage('Connected to server: ' + frame, 'success');
            updateUI();
        },
        onStompError: function (frame) {
            isConnected = false;
            addMessage('Connection error: ' + frame.headers['message'], 'error');
            updateUI();
        },
        onWebSocketError: function (error) {
            isConnected = false;
            addMessage('WebSocket error: ' + error, 'error');
            updateUI();
        }
    });

    stompClient.activate();
}

function disconnect() {
    if (stompClient !== null) {
        if (subscriptionToUpdates) {
            subscriptionToUpdates.unsubscribe();
            subscriptionToUpdates = null;
        }
        if (subscriptionToSessionState) {
            subscriptionToSessionState.unsubscribe();
            subscriptionToSessionState = null;
        }
        stompClient.deactivate();
    }
    isConnected = false;
    currentDocumentId = null;
    sessionStateSnapshot = null;
    addMessage('Disconnected from server', 'info');
    updateUI();
}

function joinDocument(options) {
    const docId = document.getElementById('documentId').value.trim();
    if (!stompClient || !docId) {
        alert('Please connect first and enter a document ID');
        return;
    }

    currentDocumentId = docId;

    // Subscribe to updates for this document
    subscriptionToUpdates = stompClient.subscribe('/topic/updates/' + currentDocumentId, function (message) {
        console.log(`Time: ${(new Date(Date.now())).toISOString()}. (/topic/updates/${currentDocumentId}) Message received: ${message.body}`);

        try {
            const sessionEvent = JSON.parse(message.body);

            processSessionEvent(sessionEvent);

            addMessage(`Received event: ${sessionEvent.type} (seq: ${sessionEvent.sequenceNumber})`, 'event');
            updateEventsDisplay();
        } catch (error) {
            console.error('Error parsing session event:', error);
            addMessage('Error parsing session event: ' + error.message, 'error');
        }
    });

    if (!stompClient) throw new Error('Client not connected');

    addMessage('Joined document: ' + docId, 'success');
    updateUI();
}

function updateMyCursorPosition(newPosition) {
    myCursorPosition = newPosition;
}

function sentChangeCursorPosition(newPosition) {
    if (!stompClient || !sessionStateSnapshot || !sessionStateSnapshot.id || !myWriterId) throw new Error('Client not connected or session data does not exist');

    addCursorPositionChangedEvent(newPosition);

    document.getElementById('cursorInfo').textContent = `Cursor: position ${newPosition}`;
    updateEventsDisplay();
}

function incrementAndGetLastSessionEventSequenceNumber() {
    return ++lastSessionEventSequenceNumber
}

function addCursorPositionChangedEvent(newPosition) {
    const cursorPositionChangedEvent = {
        type: cursorPositionChangedType,
        sessionId: sessionStateSnapshot.id,
        writerId: myWriterId,
        sequenceNumber: incrementAndGetLastSessionEventSequenceNumber(),
        newPosition: newPosition,
        broadcasted: false
    };
    processSessionEvent(cursorPositionChangedEvent);
}

function addTextAddedEvent(position, addedText) {
    const textAddedEvent = {
        type: textAddedType,
        sessionId: sessionStateSnapshot.id,
        writerId: myWriterId,
        sequenceNumber: incrementAndGetLastSessionEventSequenceNumber(),
        position: position,
        text: addedText,
        broadcasted: false
    };
    processSessionEvent(textAddedEvent);
}

function addTextRemovedEvent(position, removedLength) {
    const textRemovedEvent = {
        type: textRemovedType,
        sessionId: sessionStateSnapshot.id,
        writerId: myWriterId,
        sequenceNumber: incrementAndGetLastSessionEventSequenceNumber(),
        position: position,
        length: removedLength,
        broadcasted: false
    };
    processSessionEvent(textRemovedEvent);
}

// Helper function for text operations
function setupEventListeners() {
    // Check if element exists before setting up listeners
    const documentContent = document.getElementById('documentContent');
    if (!documentContent) return; // Exit if element doesn't exist

    // Event listeners
    documentContent.addEventListener('click', function() {
        const textarea = document.getElementById('documentContent');
        const newPosition = textarea.selectionStart;
        if(myCursorPosition === newPosition) return;
        updateMyCursorPosition(newPosition);
        sentChangeCursorPosition(newPosition);
        //processCollaborativeEvent();
    });

    // Auto-detect text operations from textarea
    let lastContent = '';
    documentContent.addEventListener('input', function(e) {
        const currentContent = e.target.value;
        const cursorPos = e.target.selectionStart;

        if (currentContent.length > lastContent.length) {
            const addedTextLength = currentContent.length - lastContent.length;
            const position = cursorPos - addedTextLength;
            const addedText = currentContent.substring(position, position + addedTextLength);

            if (!stompClient || !sessionStateSnapshot || !sessionStateSnapshot.id || !myWriterId) throw new Error('Client not connected or session data does not exist');

            addTextAddedEvent(position, addedText);
            updateEventsDisplay();

            addMessage(`Auto-sent add-text: "${addedText}" at position ${position}`, 'auto');
        } else if (currentContent.length < lastContent.length) {
            if (!stompClient || !sessionStateSnapshot || !sessionStateSnapshot.id || !myWriterId) throw new Error('Client not connected or session data does not exist');
            const removedLength = lastContent.length - currentContent.length;
            const position = cursorPos;
            addTextRemovedEvent(position, removedLength);
            updateEventsDisplay();

            addMessage(`Auto-sent remove-text: ${removedLength} chars from position ${position}`, 'auto');
        }

        lastContent = currentContent;
    });
}

// Initialize only if we're in a browser environment
if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    // Make functions globally available for HTML onclick handlers
    window.connect = connect;
    window.disconnect = disconnect;
    window.joinDocument = joinDocument;
    window.clearMessages = clearMessages;
    window.clearEvents = clearEvents;

    // Initialize UI and event listeners
    updateUI();
    updateEventsDisplay();
    setupEventListeners();
    addMessage('Application initialized. Enter User ID and Document ID, then connect.', 'info');
}

// Export functions for testing (ES modules)
export {
    sessionEvents,
    eventIsRemote,
    eventIsLocal,
    findInsertionPosition,
    insertEvent,
    processSessionEvent,
    clearEvents
};