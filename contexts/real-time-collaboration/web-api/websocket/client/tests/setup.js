// Jest setup file
import { jest } from '@jest/globals';

// Mock console methods to avoid noise in tests
global.console = {
  ...console,
  log: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};

// Setup JSDOM globals
global.alert = jest.fn();
global.confirm = jest.fn();

// Mock DOM methods that might not exist in JSDOM
global.requestAnimationFrame = jest.fn((cb) => setTimeout(cb, 0));
global.cancelAnimationFrame = jest.fn((id) => clearTimeout(id));

// Improve JSDOM cleanup
global.jsdomCleanup = () => {
  // Clear any remaining timers
  jest.clearAllTimers();

  // Clear mocks between tests
  jest.clearAllMocks();

  // Reset any global state that might interfere
  if (global.document) {
    // Remove event listeners that might persist
    const allElements = global.document.querySelectorAll('*');
    allElements.forEach(el => {
      el.replaceWith(el.cloneNode(true));
    });
  }
};