import { describe, test, expect, beforeEach, jest } from '@jest/globals';
import {
    collaborativeEvents,
    eventIsRemote,
    eventIsLocal,
    processCollaborativeEvent,
    clearEvents
} from '../collaborative-events.js';

describe('Collaborative Events', () => {
    beforeEach(() => {
        clearEvents();
        jest.clearAllMocks();
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

            processCollaborativeEvent(event1);
            processCollaborativeEvent(event2); // Should be skipped

            expect(collaborativeEvents).toHaveLength(1);
            expect(collaborativeEvents[0].text).toBe('hello');
        });

        test('should allow different remote events with different sequence numbers', () => {
            const event1 = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const event2 = { sequenceNumber: 2, broadcasted: true, type: 'TextAdded' };

            processCollaborativeEvent(event1);
            processCollaborativeEvent(event2);

            expect(collaborativeEvents).toHaveLength(2);
        });

        test('should allow local events even with duplicate sequence numbers', () => {
            const remoteEvent = { sequenceNumber: 1, broadcasted: true, type: 'TextAdded' };
            const localEvent = { sequenceNumber: 1, broadcasted: false, type: 'TextAdded' };

            processCollaborativeEvent(remoteEvent);
            processCollaborativeEvent(localEvent);

            expect(collaborativeEvents).toHaveLength(2);
        });

        test('should log warning when skipping duplicate remote event', () => {
            const consoleSpy = jest.spyOn(console, 'log');

            const event1 = { sequenceNumber: 5, broadcasted: true };
            const event2 = { sequenceNumber: 5, broadcasted: true };

            processCollaborativeEvent(event1);
            processCollaborativeEvent(event2);

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
                expect(() => processCollaborativeEvent(event))
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
                expect(() => processCollaborativeEvent(event)).not.toThrow();
            });

            expect(collaborativeEvents).toHaveLength(3);
        });
    });
});