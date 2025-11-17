-- ===================================
-- Script để tạo dữ liệu mẫu cho testing
-- ===================================

-- Tạo test users với BCrypt hashed passwords
-- Password: "Test123" -> BCrypt hash (sẽ khác mỗi lần hash do salt)
-- Bạn cần hash password trước khi insert hoặc dùng API để tạo user

-- Sample Users (bạn cần hash password trước)
-- Username: testuser1, Password: Test123
-- Username: testuser2, Password: Pass456

INSERT INTO users (user_name, hash_password) 
VALUES 
('testuser1', '$2a$10$YourBCryptHashedPasswordHere'),
('testuser2', '$2a$10$YourBCryptHashedPasswordHere');

-- Sample Products
INSERT INTO products (product_name, price, quantity, description, category)
VALUES
('Laptop Dell XPS 13', 25000000, 10, 'High-performance laptop with Intel i7', 'Electronics'),
('iPhone 15 Pro', 30000000, 15, 'Latest iPhone with A17 chip', 'Electronics'),
('Spring Boot in Action', 500000, 50, 'Comprehensive guide to Spring Boot', 'Books'),
('Clean Code', 450000, 30, 'Robert C. Martin coding principles', 'Books'),
('Nike Air Max', 3500000, 25, 'Comfortable running shoes', 'Clothing'),
('Adidas T-Shirt', 500000, 100, 'Cotton sports t-shirt', 'Clothing'),
('LEGO Star Wars', 2500000, 20, 'Ultimate collector series', 'Toys'),
('PS5 Controller', 1500000, 40, 'DualSense wireless controller', 'Toys'),
('Organic Rice 5kg', 150000, 200, 'Premium jasmine rice', 'Groceries'),
('Fresh Milk 1L', 35000, 150, 'Pasteurized fresh milk', 'Groceries');

-- Verify data
SELECT * FROM users;
SELECT * FROM products;
