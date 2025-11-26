/**
 * main.jsx - Application Entry Point
 * 
 * Purpose:
 * This is the entry point for the React application. It initializes the React app,
 * mounts it to the DOM, and configures the root-level settings like StrictMode.
 * 
 * Execution Flow:
 * 1. Browser loads index.html
 * 2. index.html includes <div id="root"></div>
 * 3. Vite bundles and loads this main.jsx file
 * 4. createRoot() finds the 'root' div
 * 5. render() mounts the React component tree
 * 6. App component and its children are displayed
 * 
 * React 18 API:
 * This file uses React 18's new createRoot API (replacing ReactDOM.render):
 * - Better concurrent features support
 * - Improved error handling
 * - Automatic batching of state updates
 * - Better TypeScript support
 * 
 * Key Imports:
 * 
 * 1. React & StrictMode:
 *    - React: Core React library
 *    - StrictMode: Development tool that highlights potential problems
 * 
 * 2. createRoot:
 *    - React 18 API for creating root DOM node
 *    - Replaces legacy ReactDOM.render()
 *    - Enables concurrent features
 * 
 * 3. App Component:
 *    - Root component of the application
 *    - Contains all other components
 * 
 * 4. Styles:
 *    - index.css: Global base styles
 *    - Imported before App for proper CSS cascade
 * 
 * StrictMode Benefits:
 * 
 * StrictMode is a development-only wrapper that:
 * ✓ Identifies components with unsafe lifecycles
 * ✓ Warns about legacy string ref API usage
 * ✓ Warns about deprecated findDOMNode usage
 * ✓ Detects unexpected side effects (double-invoking functions)
 * ✓ Detects legacy context API
 * ✓ Ensures reusable state (React 18+)
 * 
 * Important: StrictMode does NOT:
 * ✗ Affect production build (automatically removed)
 * ✗ Render any visible UI
 * ✗ Impact performance in production
 * 
 * Why Double Rendering in Development?
 * In StrictMode, React intentionally double-invokes:
 * - Function components
 * - useState, useMemo, useReducer initializers
 * - Constructor, render, and shouldComponentUpdate
 * 
 * This helps detect side effects that should be in useEffect.
 * Example console.log in component body will appear twice:
 * <pre>
 * function MyComponent() {
 *   console.log('Rendering'); // Appears twice in dev
 *   return <div>Hello</div>;
 * }
 * </pre>
 * 
 * DOM Mounting:
 * 
 * document.getElementById('root') finds:
 * <pre>
 * <!-- index.html -->
 * <body>
 *   <div id="root"></div>
 *   <script type="module" src="/src/main.jsx"></script>
 * </body>
 * </pre>
 * 
 * If 'root' element is missing, app will not render and console shows error:
 * "Target container is not a DOM element"
 * 
 * Build Process (Vite):
 * 
 * Development (npm run dev):
 * 1. Vite starts dev server on http://localhost:5173
 * 2. ESM imports resolved on-the-fly
 * 3. Hot Module Replacement (HMR) enabled
 * 4. Fast refresh on file changes
 * 
 * Production (npm run build):
 * 1. Vite bundles all files
 * 2. Tree-shaking removes unused code
 * 3. StrictMode removed automatically
 * 4. Output to dist/ folder:
 *    - dist/index.html
 *    - dist/assets/index-[hash].js
 *    - dist/assets/index-[hash].css
 * 
 * Error Boundaries:
 * To catch errors in production, wrap App with ErrorBoundary:
 * <pre>
 * {@code
 * import { ErrorBoundary } from 'react-error-boundary';
 * 
 * createRoot(document.getElementById('root')).render(
 *   <StrictMode>
 *     <ErrorBoundary fallback={<ErrorFallback />}>
 *       <App />
 *     </ErrorBoundary>
 *   </StrictMode>
 * );
 * }
 * </pre>
 * 
 * Provider Pattern:
 * For global state management, wrap with Context Providers:
 * <pre>
 * {@code
 * import { AuthProvider } from './context/AuthContext';
 * import { ThemeProvider } from './context/ThemeContext';
 * 
 * createRoot(document.getElementById('root')).render(
 *   <StrictMode>
 *     <AuthProvider>
 *       <ThemeProvider>
 *         <App />
 *       </ThemeProvider>
 *     </AuthProvider>
 *   </StrictMode>
 * );
 * }
 * </pre>
 * 
 * Performance Monitoring:
 * To measure performance, use React Profiler:
 * <pre>
 * {@code
 * import { Profiler } from 'react';
 * 
 * function onRenderCallback(id, phase, actualDuration) {
 *   console.log(`${id} (${phase}) took ${actualDuration}ms`);
 * }
 * 
 * createRoot(document.getElementById('root')).render(
 *   <Profiler id="App" onRender={onRenderCallback}>
 *     <App />
 *   </Profiler>
 * );
 * }
 * </pre>
 * 
 * Troubleshooting:
 * 
 * Issue: "Cannot find element with id 'root'"
 * Solution: Ensure index.html has <div id="root"></div>
 * 
 * Issue: App not updating on code changes
 * Solution: Check Vite HMR is working, restart dev server
 * 
 * Issue: Blank page in production
 * Solution: Check console for errors, verify build output in dist/
 * 
 * @see App.jsx - Root component
 * @see index.html - HTML entry point
 * @see vite.config.js - Build configuration
 */

import React from 'react'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
