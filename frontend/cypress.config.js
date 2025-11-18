const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    // Test environment configuration
    baseUrl: 'http://localhost:5174', // Updated to use available port
    viewportWidth: 1280,
    viewportHeight: 720,
    
    // Timeout settings
    pageLoadTimeout: 30000,
    requestTimeout: 15000,
    responseTimeout: 15000,
    defaultCommandTimeout: 10000,

    // Video and Screenshot
    video: false,
    screenshotOnRunFailure: true,
    videoOnFailure: false,

    // Test configuration
    specPattern: 'cypress/e2e/**/*.cy.js',
    supportFile: 'cypress/support/e2e.js',

    // Browser settings
    chromeWebSecurity: false,
    
    // Retries
    retries: {
      runMode: 1,
      openMode: 0
    }
  },

  // Component testing (optional)
  component: {
    devServer: {
      framework: 'react',
      bundler: 'vite',
    },
    specPattern: 'cypress/component/**/*.cy.jsx',
    supportFile: 'cypress/support/component.js'
  }
});
