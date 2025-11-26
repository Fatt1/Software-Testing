/**
 * @component
 * @example
 * // Rendered by main.jsx:
 * // <App />
 *
 * @see ProductManagement - Main feature component
 * @see LoginForm - Authentication component (commented out)
 * @see main.jsx - Application entry point that renders App
 */

import "./App.css";
import LoginForm from "./components/LoginForm";
import ProductManagement from "./components/ProductManagement";

function App() {
  return (
    <>
      <LoginForm />
    </>
  );
}

export default App;
