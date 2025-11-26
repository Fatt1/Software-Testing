/**
 * @see App.jsx - Root component
 * @see index.html - HTML entry point
 * @see vite.config.js - Build configuration
 */

import React from "react";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
