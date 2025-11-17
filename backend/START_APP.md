# Hướng dẫn chạy ứng dụng Code First

## Yêu cầu

1. ✅ SQL Server đang chạy trên `localhost:1433`
2. ✅ Có user `sa` với password `123`
3. ✅ Entities đã có JPA annotations (@Entity, @Table, @Column, etc.)
4. ✅ `spring.jpa.hibernate.ddl-auto=update` trong application.properties

## Các bước thực hiện

### Bước 1: Đảm bảo SQL Server đang chạy

Mở **SQL Server Configuration Manager** hoặc **Services** và kiểm tra:

- `SQL Server (MSSQLSERVER)` hoặc `SQL Server (instance_name)` đang ở trạng thái **Running**

### Bước 2: Tạo database (nếu chưa có)

**Option A: Tự động tạo** (đã config trong application.properties)

- Application.properties đã có `createDatabaseIfNotExist=true`
- Database sẽ tự động tạo khi chạy app lần đầu

**Option B: Tạo thủ công bằng SSMS**

```sql
-- Kết nối tới SQL Server bằng SSMS
-- Chạy query:
CREATE DATABASE MyDatabase;
GO
```

### Bước 3: Chạy Spring Boot application

#### Cách 1: Dùng Maven Wrapper

```bash
cd "d:/software testing/backend"
./mvnw.cmd spring-boot:run
```

#### Cách 2: Dùng IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Mở project trong IDE
2. Tìm file `FloginApplication.java`
3. Click chuột phải → Run 'FloginApplication'

#### Cách 3: Build JAR và chạy

```bash
cd "d:/software testing/backend"
./mvnw.cmd clean package -DskipTests
java -jar target/flogin-0.0.1-SNAPSHOT.jar
```

### Bước 4: Kiểm tra logs

Khi application start, bạn sẽ thấy trong console:

```
Hibernate:
    create table users (
       id bigint identity not null,
        hash_password varchar(255) not null,
        user_name varchar(50) not null,
        primary key (id)
    )

Hibernate:
    create table products (
       id bigint identity not null,
        category varchar(50) not null,
        description varchar(500),
        price float(53) not null,
        product_name varchar(100) not null,
        quantity integer not null,
        primary key (id)
    )

Hibernate:
    alter table users
       add constraint UK_xxx unique (user_name)
```

✅ **Nếu thấy DDL statements → Tables đã được tạo thành công!**

### Bước 5: Verify trong SQL Server

Mở **SQL Server Management Studio (SSMS)** và chạy:

```sql
-- Kiểm tra database
USE MyDatabase;
GO

-- Xem danh sách tables
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE';

-- Xem cấu trúc bảng users
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'users'
ORDER BY ORDINAL_POSITION;

-- Xem cấu trúc bảng products
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'products'
ORDER BY ORDINAL_POSITION;

-- Xem constraints
SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_NAME IN ('users', 'products');
```

## Kết quả mong đợi

### Table: users

| Column        | Type         | Nullable | Constraint            |
| ------------- | ------------ | -------- | --------------------- |
| id            | bigint       | NOT NULL | PRIMARY KEY, IDENTITY |
| user_name     | varchar(50)  | NOT NULL | UNIQUE                |
| hash_password | varchar(255) | NOT NULL | -                     |

### Table: products

| Column       | Type         | Nullable | Constraint            |
| ------------ | ------------ | -------- | --------------------- |
| id           | bigint       | NOT NULL | PRIMARY KEY, IDENTITY |
| category     | varchar(50)  | NOT NULL | -                     |
| description  | varchar(500) | NULL     | -                     |
| price        | float        | NOT NULL | -                     |
| product_name | varchar(100) | NOT NULL | -                     |
| quantity     | int          | NOT NULL | -                     |

## Troubleshooting

### Lỗi: "Cannot create PoolableConnectionFactory"

**Nguyên nhân:** SQL Server chưa chạy hoặc connection string sai

**Giải pháp:**

1. Kiểm tra SQL Server service đang chạy
2. Verify port 1433 đang open: `netstat -an | findstr 1433`
3. Kiểm tra username/password trong application.properties

### Lỗi: "Login failed for user 'sa'"

**Nguyên nhân:** Sai password hoặc user 'sa' bị disable

**Giải pháp:**

1. Reset password cho user 'sa'
2. Enable SQL Server Authentication mode
3. Restart SQL Server service

### Lỗi: "Database 'MyDatabase' does not exist"

**Nguyên nhân:** Database chưa được tạo

**Giải pháp:**

1. Tạo database thủ công: `CREATE DATABASE MyDatabase;`
2. Hoặc đảm bảo có `createDatabaseIfNotExist=true` trong connection string

### Tables không được tạo

**Nguyên nhân:** `ddl-auto` config sai hoặc entities không được scan

**Giải pháp:**

1. Kiểm tra `spring.jpa.hibernate.ddl-auto=update`
2. Verify entities có `@Entity` annotation
3. Đảm bảo entities nằm trong package `com.flogin` hoặc sub-packages
4. Check logs có error message không

### Lỗi: "The TCP/IP connection to the host localhost, port 1433 has failed"

**Nguyên nhân:** TCP/IP protocol chưa được enable trong SQL Server

**Giải pháp:**

1. Mở **SQL Server Configuration Manager**
2. Vào **SQL Server Network Configuration** → **Protocols**
3. Enable **TCP/IP**
4. Restart SQL Server service

## Thêm test data (Optional)

Sau khi tables được tạo, bạn có thể thêm test data:

```sql
USE MyDatabase;
GO

-- Insert test users với BCrypt hash password
-- Password: "Test123" (đã được hash bằng BCryptPasswordEncoder)
INSERT INTO users (user_name, hash_password)
VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
    ('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi');

-- Insert test products
INSERT INTO products (product_name, category, description, price, quantity)
VALUES
    ('Laptop Dell XPS 13', 'Electronics', 'High-end ultrabook', 25000000, 10),
    ('The Great Gatsby', 'Books', 'Classic novel', 150000, 50),
    ('T-Shirt Nike', 'Clothing', 'Cotton t-shirt', 350000, 100),
    ('LEGO Star Wars', 'Toys', 'Building blocks set', 1200000, 20),
    ('Rice 5kg', 'Groceries', 'Premium rice', 120000, 200);

-- Verify data
SELECT * FROM users;
SELECT * FROM products;
```

## Next Steps

Sau khi tables được tạo thành công:

1. ✅ **Test CRUD operations** qua REST API (AuthController, ProductController)
2. ✅ **Chạy unit tests** để verify business logic
3. ✅ **Test authentication** với JWT tokens
4. ✅ **Thêm sample data** để test frontend integration
5. 📝 **Document API endpoints** (có thể dùng Swagger/OpenAPI)

## Useful Commands

```bash
# Clean và compile
./mvnw.cmd clean compile

# Chạy tất cả tests
./mvnw.cmd test

# Chạy specific test class
./mvnw.cmd test -Dtest=AuthServiceTest

# Build JAR file
./mvnw.cmd clean package -DskipTests

# Chạy application
./mvnw.cmd spring-boot:run

# Xem logs với debug level
./mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.hibernate.SQL=DEBUG
```
