import { describe, test, expect, beforeEach, afterEach } from '@jest/globals';
import {
    collaborativeEvents,
    findInsertionPosition,
    insertEvent,
    processCollaborativeEvent,
    clearEvents
} from '../collaborative-events.js';

describe('Binary Search Insertion', () => {
    beforeEach(() => {
        clearEvents();
    });

    afterEach(() => {
        // Clean up after each test to prevent interference
        clearEvents();

        // Call the global cleanup function if available
        if (global.jsdomCleanup) {
            global.jsdomCleanup();
        }
    });

    describe('findInsertionPosition', () => {
        test('should return 0 for empty array', () => {
            const position = findInsertionPosition(5);
            expect(position).toBe(0);
        });

        test('should find correct insertion position for single element', () => {
            // Add one element first
            insertEvent(0, { sequenceNumber: 5 });

            expect(findInsertionPosition(3)).toBe(0); // Insert before
            expect(findInsertionPosition(7)).toBe(1); // Insert after
            expect(findInsertionPosition(5)).toBe(0); // Insert before duplicate
        });

        test('should maintain sorted order with multiple insertions', () => {
            const events = [
                { sequenceNumber: 1, broadcasted: false },
                { sequenceNumber: 5, broadcasted: false },
                { sequenceNumber: 3, broadcasted: false },
                { sequenceNumber: 7, broadcasted: false },
                { sequenceNumber: 2, broadcasted: false }
            ];

            events.forEach(event => processCollaborativeEvent(event));

            // Verify array is sorted by sequence number
            const sequenceNumbers = collaborativeEvents.map(e => e.sequenceNumber);
            expect(sequenceNumbers).toEqual([1, 2, 3, 5, 7]);
        });

        test('should find correct position in sorted array', () => {
            // Setup sorted array: [1, 3, 5, 7, 9]
            [1, 3, 5, 7, 9].forEach(seq => {
                insertEvent(collaborativeEvents.length, { sequenceNumber: seq });
            });

            expect(findInsertionPosition(0)).toBe(0);  // Before all
            expect(findInsertionPosition(2)).toBe(1);  // Between 1 and 3
            expect(findInsertionPosition(4)).toBe(2);  // Between 3 and 5
            expect(findInsertionPosition(6)).toBe(3);  // Between 5 and 7
            expect(findInsertionPosition(8)).toBe(4);  // Between 7 and 9
            expect(findInsertionPosition(10)).toBe(5); // After all
        });

        test('should handle duplicate sequence numbers correctly', () => {
            // Insert multiple events with the same sequence number
            [1, 3, 3, 3, 5].forEach(seq => {
                insertEvent(collaborativeEvents.length, { sequenceNumber: seq });
            });

            // Should insert at the beginning of the duplicate group
            const position = findInsertionPosition(3);
            expect(position).toBe(1); // Before the first 3
        });
    });

    describe('Event Ordering Integration', () => {
        test('should maintain chronological order when processing events out of sequence', () => {
            const events = [
                { sequenceNumber: 10, broadcasted: false, type: 'Event10' },
                { sequenceNumber: 5, broadcasted: false, type: 'Event5' },
                { sequenceNumber: 15, broadcasted: false, type: 'Event15' },
                { sequenceNumber: 1, broadcasted: false, type: 'Event1' },
                { sequenceNumber: 8, broadcasted: false, type: 'Event8' }
            ];

            events.forEach(event => processCollaborativeEvent(event));

            const expectedOrder = [1, 5, 8, 10, 15];
            const actualOrder = collaborativeEvents.map(e => e.sequenceNumber);

            expect(actualOrder).toEqual(expectedOrder);
            expect(collaborativeEvents[0].type).toBe('Event1');
            expect(collaborativeEvents[4].type).toBe('Event15');
        });

        test('should handle mixed remote and local events correctly', () => {
            const events = [
                { sequenceNumber: 3, broadcasted: true, type: 'Remote3' },
                { sequenceNumber: 1, broadcasted: false, type: 'Local1' },
                { sequenceNumber: 2, broadcasted: true, type: 'Remote2' },
                { sequenceNumber: 4, broadcasted: false, type: 'Local4' }
            ];

            events.forEach(event => processCollaborativeEvent(event));

            const types = collaborativeEvents.map(e => e.type);
            expect(types).toEqual(['Local1', 'Remote2', 'Remote3', 'Local4']);
        });
    });

    describe('Performance with Large Arrays', () => {
        test('should handle large number of events efficiently', () => {
            // Insert 1000 events in reverse order
            for (let i = 999; i >= 0; i--) {
                processCollaborativeEvent({
                    sequenceNumber: i,
                    broadcasted: false,
                    type: `Event${i}`
                });
            }

            // Verify all events are in correct order
            expect(collaborativeEvents).toHaveLength(1000);

            for (let i = 0; i < 1000; i++) {
                expect(collaborativeEvents[i].sequenceNumber).toBe(i);
            }
        });
    });
});