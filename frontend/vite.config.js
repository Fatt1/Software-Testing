/**
 * Vite Configuration File
 * 
 * Vite is a modern build tool that provides fast development server
 * with Hot Module Replacement (HMR) and optimized production builds.
 * 
 * Features:
 * - Lightning fast dev server with instant HMR
 * - Optimized build output using Rollup
 * - Native ES modules support
 * - Built-in TypeScript and JSX support
 * - Plugin ecosystem for extended functionality
 * 
 * Development Server:
 * - Command: npm run dev
 * - Default port: 5173
 * - Hot reload on file changes
 * - Fast refresh for React components
 * 
 * Production Build:
 * - Command: npm run build
 * - Output directory: dist/
 * - Minified and optimized bundles
 * - Tree-shaking for smaller bundle size
 * 
 * @see https://vitejs.dev/config/
 */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

/**
 * Export Vite configuration
 * 
 * Plugins:
 * - @vitejs/plugin-react: Enables React Fast Refresh and JSX support
 *   - Automatic JSX transformation
 *   - React component HMR
 *   - Development error overlay
 * 
 * Default Configuration:
 * - Server port: 5173 (can be changed with --port flag)
 * - Build output: dist/
 * - Public directory: public/
 * - Source maps enabled in development
 */
export default defineConfig({
  plugins: [
    react() // Enable React support with Fast Refresh
  ],
})
