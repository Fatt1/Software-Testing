import "./App.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import ProductManagement from "./components/ProductManagement";

function App() {
  return (
    // Bọc ứng dụng trong BrowserRouter
    <BrowserRouter>
      <Routes>
        {/* Đường dẫn mặc định "/" sẽ chuyển hướng về "/login" */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* Khi URL là /login thì hiện LoginForm */}
        <Route path="/login" element={<LoginForm />} />
        
        {/* Khi URL là /products thì hiện ProductManagement */}
        <Route path="/products" element={<ProductManagement />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;