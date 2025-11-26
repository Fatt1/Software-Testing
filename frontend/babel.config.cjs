/**
 * Babel Configuration File - JavaScript Transpilation Settings
 * 
 * Babel is a JavaScript compiler that transforms modern JavaScript/JSX
 * into backwards-compatible code that can run in older environments.
 * 
 * This configuration is primarily used by Jest for testing,
 * as Vite handles transpilation for development/production builds.
 * 
 * Key Responsibilities:
 * - Transpile JSX to JavaScript
 * - Transform ES6+ syntax for Node.js (Jest)
 * - Handle import.meta syntax in tests
 * - Instrument code for coverage reports
 * 
 * Presets:
 * 1. @babel/preset-env - Transpile modern JavaScript for target environment
 * 2. @babel/preset-react - Transform JSX syntax to React.createElement()
 * 
 * Plugins:
 * 1. @babel/plugin-syntax-import-meta - Support import.meta in tests
 * 2. istanbul (test only) - Code coverage instrumentation
 * 
 * @see https://babeljs.io/docs/configuration
 */

/**
 * Babel Presets Configuration
 * 
 * Presets are collections of Babel plugins that work together
 * to transform specific types of code.
 */
const presets = [
  /**
   * @babel/preset-env - Smart transpilation based on target environment
   * 
   * Configuration:
   * - targets: { node: 'current' }
   *   Transpiles for the current Node.js version running Jest
   *   Ensures compatibility with test environment
   * 
   * Features:
   * - Automatically determines required transformations
   * - Only includes necessary polyfills
   * - Optimizes for target environment
   */
  ['@babel/preset-env', { targets: { node: 'current' } }],
  
  /**
   * @babel/preset-react - JSX and React transformations
   * 
   * Configuration:
   * - runtime: 'automatic'
   *   Uses new JSX transform (React 17+)
   *   No need to import React in every file
   *   Automatically imports required JSX functions
   * 
   * Transforms:
   * - JSX syntax: <div> → React.createElement('div')
   * - JSX attributes: className, onClick, etc.
   * - JSX fragments: <> → React.Fragment
   */
  ['@babel/preset-react', { runtime: 'automatic' }],
];

/**
 * Babel Plugins Configuration
 * 
 * Plugins are individual transformations that can be applied to code.
 */
const plugins = [
  /**
   * @babel/plugin-syntax-import-meta
   * 
   * Enables support for import.meta syntax in tests.
   * import.meta provides metadata about the current module,
   * commonly used in Vite applications.
   * 
   * Example: import.meta.env.VITE_API_URL
   */
  ['@babel/plugin-syntax-import-meta'],
];

/**
 * Add Istanbul plugin for code coverage in test environment
 * 
 * Istanbul instruments code to track which lines are executed during tests,
 * generating detailed coverage reports showing:
 * - Lines covered/uncovered
 * - Branches taken/not taken
 * - Functions called/not called
 * - Statements executed/not executed
 * 
 * Only enabled during testing to avoid overhead in development/production.
 */
if (process.env.NODE_ENV === 'test') {
  plugins.push(['istanbul']);
}

// Export configuration for Babel and Jest
module.exports = { presets, plugins };
