/**
 * Vite Configuration File
 * @see https://vitejs.dev/config/
 */
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
export default defineConfig({
  plugins: [
    react(), // Enable React support with Fast Refresh
  ],
});
