import { describe, test, expect, beforeEach, afterEach, jest } from '@jest/globals';
import {
    sessionEvents,
    eventIsRemote,
    eventIsLocal,
    processSessionEvent,
    clearEvents, insertEvent
} from '../session-events.js';

describe('Session Events', () => {
    beforeEach(() => {
        clearEvents();
        jest.clearAllMocks();
    });

    afterEach(() => {
        // Clean up after each test to prevent interference
        clearEvents();
        jest.clearAllMocks();

        // Call the global cleanup function if available
        if (global.jsdomCleanup) {
            global.jsdomCleanup();
        }

        // Ensure globals are properly restored if any test modified them
        if (typeof window !== 'undefined' && typeof document !== 'undefined') {
            // Reset any event listeners or DOM state that might persist
            if (document.body) {
                document.body.innerHTML = '';
            }
        }
    });

    describe('Event Type Detection', () => {
        test('should identify remote events correctly', () => {
            const remoteEvent = { broadcasted: true };
            const localEvent = { broadcasted: false };

            expect(eventIsRemote(remoteEvent)).toBe(true);
            expect(eventIsRemote(localEvent)).toBe(false);
        });

        test('should identify local events correctly', () => {
            const remoteEvent = { broadcasted: true };
            const localEvent = { broadcasted: false };

            expect(eventIsLocal(remoteEvent)).toBe(false);
            expect(eventIsLocal(localEvent)).toBe(true);
        });

        test('should throw error for undefined broadcasted property', () => {
            const invalidEvent = {};

            expect(() => eventIsRemote(invalidEvent)).toThrow('event.broadcasted is undefined or null');
        });
    });

    describe('Duplicate Remote Event Prevention', () => {
        test('should prevent duplicate remote events with same sequence number', () => {
            const event1 = {
                sequenceNumber: 1,
                broadcasted: true,
                type: 'TextAdded',
                text: 'hello'
            };
            const event2 = {
                sequenceNumber: 1,
                broadcasted: true,
                type: 'TextAdded',
                text: 'world' // Different content but same sequence number
            };

            processSessionEvent(event1);
            processSessionEvent(event2); // Should be skipped

            expect(sessionEvents).toHaveLength(1);
            expect(sessionEvents[0].text).toBe('hello');
        });

        test('should allow different remote events with different sequence numbers', () => {
            const event1 = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const event2 = { sequenceNumber: 2, broadcasted: true, type: 'TextAdded' };

            processSessionEvent(event1);
            processSessionEvent(event2);

            expect(sessionEvents).toHaveLength(2);
        });

        test('should allow local events even with duplicate sequence numbers', () => {
            const remoteEvent = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const localEvent = { sequenceNumber: 1, broadcasted: false, type: 'TextAdded' };

            processSessionEvent(remoteEvent);
            processSessionEvent(localEvent);

            expect(sessionEvents).toHaveLength(2);
        });

        test('should log warning when skipping duplicate remote event', () => {
            const consoleSpy = jest.spyOn(console, 'log');

            const event1 = { sequenceNumber: 5, broadcasted: true };
            const event2 = { sequenceNumber: 5, broadcasted: true };

            processSessionEvent(event1);
            processSessionEvent(event2);

            expect(consoleSpy).toHaveBeenCalledWith(
                'Remote event with sequence number 5 already exists, skipping insertion'
            );
        });
    });

    describe('Sequence Number Validation', () => {
        test('should throw error for remote events with invalid sequence numbers', () => {
            const invalidEvents = [
                { sequenceNumber: undefined, broadcasted: true },
                { sequenceNumber: null, broadcasted: true },
                { sequenceNumber: 'invalid', broadcasted: true },
                { sequenceNumber: -1, broadcasted: true }
            ];

            invalidEvents.forEach(event => {
                expect(() => processSessionEvent(event))
                    .toThrow('Event sequence number must be a number greater than or equal to 0');
            });
        });

        test('should accept valid sequence numbers for remote events', () => {
            const validEvents = [
                { sequenceNumber: 0, broadcasted: true },
                { sequenceNumber: 1, broadcasted: true },
                { sequenceNumber: 100, broadcasted: true }
            ];

            validEvents.forEach(event => {
                expect(() => processSessionEvent(event)).not.toThrow();
            });

            expect(sessionEvents).toHaveLength(3);
        });
    });

    describe('clearEvents', () => {
        test('should clear all events from the array', () => {
            const events = [
                { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' },
                { sequenceNumber: 2, broadcasted: false, type: 'TextDeleted' },
                { sequenceNumber: 3, broadcasted: true, type: 'TextAdded' }
            ];

            events.forEach((event, index) => insertEvent(index, event));
            expect(sessionEvents).toHaveLength(3);

            clearEvents();
            expect(sessionEvents).toHaveLength(0);
        });

        test('should preserve array reference after clearing', () => {
            const originalArray = sessionEvents;

            insertEvent(0, { sequenceNumber: 1, broadcasted: true });
            clearEvents();

            expect(sessionEvents).toBe(originalArray);
        });

        test('should work when array is already empty', () => {
            expect(sessionEvents).toHaveLength(0);

            expect(() => clearEvents()).not.toThrow();
            expect(sessionEvents).toHaveLength(0);
        });

        test('should not call updateEventsDisplay in non-browser environment', () => {
            // Mock the browser environment check instead of deleting globals
            const originalWindow = global.window;
            const originalDocument = global.document;

            // Temporarily set to undefined instead of deleting
            global.window = undefined;
            global.document = undefined;

            insertEvent(0, { sequenceNumber: 1, broadcasted: true });

            expect(() => clearEvents()).not.toThrow();
            expect(sessionEvents).toHaveLength(0);

            // Restore original values
            global.window = originalWindow;
            global.document = originalDocument;
        });
    });

    describe('insertEvent', () => {
        test('should insert event at the beginning of empty array', () => {
            const event = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };

            insertEvent(0, event);

            expect(sessionEvents).toHaveLength(1);
            expect(sessionEvents[0]).toBe(event);
        });

        test('should insert event at the beginning of array', () => {
            const event1 = { sequenceNumber: 2, broadcasted: true, type: 'TextAdded' };
            const event2 = { sequenceNumber: 1, broadcasted: false, type: 'TextDeleted' };

            insertEvent(0, event1);
            insertEvent(0, event2);

            expect(sessionEvents).toHaveLength(2);
            expect(sessionEvents[0]).toBe(event2);
            expect(sessionEvents[1]).toBe(event1);
        });

        test('should insert event at the end of array', () => {
            const event1 = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const event2 = { sequenceNumber: 2, broadcasted: false, type: 'TextDeleted' };

            insertEvent(0, event1);
            insertEvent(1, event2);

            expect(sessionEvents).toHaveLength(2);
            expect(sessionEvents[0]).toBe(event1);
            expect(sessionEvents[1]).toBe(event2);
        });

        test('should insert event in the middle of array', () => {
            const event1 = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const event2 = { sequenceNumber: 3, broadcasted: true, type: 'TextDeleted' };
            const event3 = { sequenceNumber: 2, broadcasted: false, type: 'TextModified' };

            insertEvent(0, event1);
            insertEvent(1, event2);
            insertEvent(1, event3);

            expect(sessionEvents).toHaveLength(3);
            expect(sessionEvents[0]).toBe(event1);
            expect(sessionEvents[1]).toBe(event3);
            expect(sessionEvents[2]).toBe(event2);
        });

        test('should handle insertion at position equal to array length', () => {
            const event1 = { sequenceNumber: 1, broadcasted: true };
            const event2 = { sequenceNumber: 2, broadcasted: false };

            insertEvent(0, event1);
            insertEvent(sessionEvents.length, event2);

            expect(sessionEvents).toHaveLength(2);
            expect(sessionEvents[0]).toBe(event1);
            expect(sessionEvents[1]).toBe(event2);
        });

        test('should maintain original array reference', () => {
            const originalArray = sessionEvents;
            const event = { sequenceNumber: 1, broadcasted: true };

            insertEvent(0, event);

            expect(sessionEvents).toBe(originalArray);
        });

        test('should insert multiple events maintaining order', () => {
            const events = [
                { sequenceNumber: 1, broadcasted: true, type: 'A' },
                { sequenceNumber: 2, broadcasted: false, type: 'B' },
                { sequenceNumber: 3, broadcasted: true, type: 'C' },
                { sequenceNumber: 4, broadcasted: false, type: 'D' }
            ];

            events.forEach((event, index) => {
                insertEvent(index, event);
            });

            expect(sessionEvents).toHaveLength(4);
            events.forEach((event, index) => {
                expect(sessionEvents[index]).toBe(event);
            });
        });
    });
});