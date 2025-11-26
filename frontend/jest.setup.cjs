/**
 * Jest Setup File - Global Test Configuration
 * 
 * This file is executed once before all tests run, setting up
 * the testing environment and importing global utilities.
 * 
 * Purpose:
 * - Import custom matchers from @testing-library/jest-dom
 * - Setup global test utilities and mocks
 * - Configure test environment settings
 * - Initialize any required test infrastructure
 * 
 * @testing-library/jest-dom provides custom matchers like:
 * - toBeInTheDocument() - Check if element exists in DOM
 * - toHaveTextContent() - Check element's text content
 * - toBeVisible() - Check if element is visible
 * - toHaveClass() - Check element's CSS classes
 * - toHaveAttribute() - Check element attributes
 * - toBeDisabled() - Check if element is disabled
 * - And many more...
 * 
 * Configuration:
 * - Referenced in package.json: "setupFilesAfterEnv": ["./jest.setup.cjs"]
 * - Loaded after Jest environment setup but before tests
 * - CommonJS format (.cjs) for compatibility with Jest
 * 
 * Example Usage in Tests:
 * ```javascript
 * expect(button).toBeInTheDocument();
 * expect(input).toHaveValue('test');
 * expect(div).toHaveClass('active');
 * ```
 * 
 * @see https://github.com/testing-library/jest-dom
 */

// Import custom Jest matchers for DOM element assertions
require('@testing-library/jest-dom');
