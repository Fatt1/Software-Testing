-- Tạo users test cho application
-- Password đã được hash bằng BCrypt
USE STDatabase;
GO

-- Tạo user admin với password: admin123
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMye1PH3Cl3xAHGCkHLRQGvh7KfQ0C9cZJy
IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin')
BEGIN
    INSERT INTO users (user_name, email, hash_password) 
    VALUES ('admin', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PH3Cl3xAHGCkHLRQGvh7KfQ0C9cZJy');
    PRINT 'Created user: admin / admin123';
END
ELSE
BEGIN
    PRINT 'User admin already exists';
END
GO

-- Tạo user test với password: 123456
-- Hash: $2a$10$slYQmyNdGzTn7ZJ6jN8D1e4S8J3hNkqVJ2TqR4LKf8w9D6N0VvGq2
IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'test')
BEGIN
    INSERT INTO users (user_name, email, hash_password)
    VALUES ('test', 'test@test.com', '$2a$10$slYQmyNdGzTn7ZJ6jN8D1e4S8J3hNkqVJ2TqR4LKf8w9D6N0VvGq2');
    PRINT 'Created user: test / 123456';
END
ELSE
BEGIN
    PRINT 'User test already exists';
END
GO

-- Tạo user01 với password: password123
-- Hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'user01')
BEGIN
    INSERT INTO users (user_name, email, hash_password)
    VALUES ('user01', 'user01@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');
    PRINT 'Created user: user01 / password123';
END
ELSE
BEGIN
    PRINT 'User user01 already exists';
END
GO

-- Hiển thị tất cả users
SELECT user_name, email FROM users;
GO
