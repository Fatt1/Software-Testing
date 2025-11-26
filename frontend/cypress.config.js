/**
 * Cypress Configuration File
 * @see https://docs.cypress.io/guides/references/configuration
 */
const { defineConfig } = require('cypress');

module.exports = defineConfig({
  /**
   * E2E Testing Configuration
   * 
   * Configures end-to-end tests that run against the full application
   * in a real browser environment.
   */
  e2e: { 
    /**
     * Base URL for all cy.visit() and cy.request() commands
     * Points to local development server
     * Change to production URL for production testing
     */
    baseUrl: 'http://localhost:5173',
    
    /**
     * Default viewport dimensions for test browser
     * Simulates desktop screen resolution
     */
    viewportWidth: 1280,
    viewportHeight: 720,
 
    /**
     * Time to wait for page to load before failing
     * Default: 60000ms (60 seconds)
     */
    pageLoadTimeout: 30000,
    
    /**
     * Time to wait for cy.request() to resolve
     * Used for API calls in tests
     */
    requestTimeout: 15000,
    
    /**
     * Time to wait for response in cy.wait()
     * Used for network request assertions
     */
    responseTimeout: 15000,
    
    /**
     * Default timeout for most Cypress commands
     * Applies to cy.get(), cy.contains(), etc.
     */
    defaultCommandTimeout: 10000,

    /**
     * Disable video recording for all tests
     * Enable for debugging: set to true
     * Videos saved to: cypress/videos/
     */
    video: false,
    
    /**
     * Take screenshot when test fails
     * Screenshots saved to: cypress/screenshots/
     * Very useful for debugging failures in CI/CD
     */
    screenshotOnRunFailure: true,
    
    /**
     * Record video only when test fails
     * Disabled here since video is disabled
     */
    videoOnFailure: false,

    /**
     * Pattern to match E2E test files
     * Looks for *.cy.js files in e2e directory
     * Example: login.cy.js, product-management.cy.js
     */
    specPattern: 'src/tests/cypress/e2e/**/*.cy.js',
    
    /**
     * Support file with custom commands and global config
     * Loaded before every test file
     * Contains custom commands like cy.login()
     */
    supportFile: 'src/tests/cypress/support/e2e.js',
    
    /**
     * Folder containing test data fixtures
     * Access via: cy.fixture('users.json')
     * Used for test data like users, products, etc.
     */
    fixturesFolder: 'src/tests/cypress/fixtures',

    /**
     * Disable Chrome web security
     * Allows cross-origin requests in tests
     * Useful for testing external APIs or CORS issues
     */
    chromeWebSecurity: false,
  
    /**
     * Retry failed tests to reduce flakiness
     * - runMode: Retries in headless mode (CI/CD)
     * - openMode: Retries in interactive mode (development)
     */
    retries: {
      runMode: 1,    // Retry once in CI/CD pipeline
      openMode: 0    // No retries in interactive mode
    }
  },

  /**
   * Component Testing Configuration (Optional)
   * 
   * Tests individual React components in isolation
   * using Vite dev server for fast feedback.
   */
  component: {
    /**
     * Dev server configuration for component testing
     * Uses Vite for fast component mounting and HMR
     */
    devServer: {
      framework: 'react',  // Framework being tested
      bundler: 'vite',     // Build tool (Vite for fast builds)
    },
    
    /**
     * Pattern to match component test files
     * Example: Button.cy.jsx, Form.cy.jsx
     */
    specPattern: 'src/tests/cypress/component/**/*.cy.jsx',
    
    /**
     * Support file for component tests
     * Contains component-specific setup and commands
     */
    supportFile: 'src/tests/cypress/support/component.js'
  }
});
