/**
 * @see https://babeljs.io/docs/configuration
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
  ["@babel/preset-env", { targets: { node: "current" } }],

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
  ["@babel/preset-react", { runtime: "automatic" }],
];

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
  ["@babel/plugin-syntax-import-meta"],
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
 */
if (process.env.NODE_ENV === "test") {
  plugins.push(["istanbul"]);
}

// Export configuration for Babel and Jest
module.exports = { presets, plugins };
