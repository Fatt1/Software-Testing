# Hướng dẫn chạy Spring Boot Application (Code First)

## 🎯 Mục tiêu

Chạy Spring Boot application để tự động tạo database tables từ JPA entities (Code First approach).

## 🚀 Quick Start (Recommended)

### Cách nhanh nhất: Chạy từ IDE

1. **Mở VS Code** (hoặc IntelliJ IDEA / Eclipse)
2. **Open Folder:** `d:\software testing\backend`
3. **Mở file:** `src/main/java/com/flogin/FloginApplication.java`
4. **Click nút ▶️ Run** bên cạnh method `main()`
5. **Xem logs** trong terminal → Tìm Hibernate DDL statements

✅ **Nếu thấy:**

```
Hibernate: create table users (...)
Hibernate: create table products (...)
Started FloginApplication in 3.456 seconds
```

→ **Thành công!** Tables đã được tạo trong SQL Server.

📖 **Chi tiết:** Xem file [`RUN_FROM_IDE.md`](RUN_FROM_IDE.md)

---

## 📁 Cấu trúc Documentation

| File                       | Nội dung                                                    |
| -------------------------- | ----------------------------------------------------------- |
| **RUN_FROM_IDE.md**        | ⭐ Hướng dẫn chạy từ VS Code/IntelliJ/Eclipse (Recommended) |
| **CODE_FIRST_GUIDE.md**    | 📚 Chi tiết về Code First approach, DDL auto modes          |
| **START_APP.md**           | 🔧 Troubleshooting, verify database, SQL queries            |
| **run-app.bat**            | 🪟 Script Windows để chạy app (nếu mvnw.cmd works)          |
| **JWT_API_MIGRATION.md**   | 🔐 JWT library API changes (0.11.x → 0.12.x)                |
| **FIX_DELETE_METHOD.md**   | 🐛 Fix deleteById() parameter type issue                    |
| **REFACTORING_SUMMARY.md** | ♻️ Bean Validation refactoring summary                      |

---

## ⚙️ Yêu cầu

### Phần mềm cần cài

- ✅ **Java Development Kit (JDK)** 21 hoặc 23
- ✅ **SQL Server** 2019+ đang chạy
- ✅ **IDE** (một trong các options):
  - VS Code + Extension Pack for Java + Spring Boot Extension Pack
  - IntelliJ IDEA (Community hoặc Ultimate)
  - Eclipse IDE / Spring Tool Suite (STS)

### SQL Server Configuration

1. **Service đang chạy:**
   - Services → SQL Server (MSSQLSERVER) → Status: Running
2. **TCP/IP enabled:**
   - SQL Server Configuration Manager → Protocols → TCP/IP: Enabled
3. **Login credentials:**
   - Username: `sa`
   - Password: `123`
   - Port: `1433`

---

## 📊 Expected Results

### Console Output (Logs)

Khi application start thành công:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.7)

2025-11-12 10:30:42.123  INFO ... : Starting FloginApplication
2025-11-12 10:30:43.456  INFO ... : No active profile set, falling back to default
2025-11-12 10:30:44.789  INFO ... : Bootstrapping Spring Data JPA repositories

Hibernate:
    create table users (
       id bigint identity not null,
        hash_password varchar(255) not null,
        user_name varchar(50) not null,
        primary key (id)
    )

Hibernate:
    alter table users
       add constraint UK_k8d0f2n7n88w1a16yhua64onx unique (user_name)

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

2025-11-12 10:30:45.123  INFO ... : Started FloginApplication in 3.456 seconds (JVM running for 4.123)
```

### Database Tables Created

Mở **SQL Server Management Studio (SSMS)** và verify:

```sql
USE MyDatabase;
GO

-- Xem tables
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
-- Output: products, users

-- Xem cấu trúc
EXEC sp_help 'users';
EXEC sp_help 'products';
```

#### Table: users

| Column        | Type         | Nullable | Constraint        |
| ------------- | ------------ | -------- | ----------------- |
| id            | bigint       | NO       | PK, IDENTITY(1,1) |
| user_name     | varchar(50)  | NO       | UNIQUE            |
| hash_password | varchar(255) | NO       | -                 |

#### Table: products

| Column       | Type         | Nullable | Constraint        |
| ------------ | ------------ | -------- | ----------------- |
| id           | bigint       | NO       | PK, IDENTITY(1,1) |
| product_name | varchar(100) | NO       | -                 |
| category     | varchar(50)  | NO       | -                 |
| description  | varchar(500) | YES      | -                 |
| price        | float        | NO       | -                 |
| quantity     | int          | NO       | -                 |

---

## 🐛 Common Issues

### Issue 1: "mvnw.cmd: command not found"

**Nguyên nhân:** Bash shell không recognize `.cmd` files

**Giải pháp:** ✅ **Chạy từ IDE** (xem `RUN_FROM_IDE.md`)

---

### Issue 2: "Files\Java\jdk-23""=="" was unexpected"

**Nguyên nhân:** JAVA_HOME có khoảng trắng trong path

**Giải pháp:** ✅ **Chạy từ IDE** (IDE tự handle JAVA_HOME)

---

### Issue 3: "Cannot create PoolableConnectionFactory"

**Nguyên nhân:** SQL Server không chạy hoặc connection string sai

**Giải pháp:**

1. Kiểm tra SQL Server service: `Services` → SQL Server (MSSQLSERVER) → Start
2. Verify connection trong SSMS: Server: `localhost`, User: `sa`, Password: `123`
3. Check `application.properties` có đúng:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MyDatabase
   spring.datasource.username=sa
   spring.datasource.password=123
   ```

---

### Issue 4: "Login failed for user 'sa'"

**Nguyên nhân:** SQL Server Authentication chưa enable hoặc sai password

**Giải pháp:**

1. Mở SSMS → Right-click server → Properties
2. Security → "SQL Server and Windows Authentication mode"
3. Restart SQL Server service
4. Reset password nếu cần:
   ```sql
   ALTER LOGIN sa WITH PASSWORD = '123';
   ALTER LOGIN sa ENABLE;
   ```

---

### Issue 5: "Port 8080 already in use"

**Nguyên nhân:** Đã có process khác dùng port 8080

**Giải pháp 1:** Đổi port trong `application.properties`:

```properties
server.port=8081
```

**Giải pháp 2:** Kill process:

```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### Issue 6: Tables không được tạo (No DDL in logs)

**Nguyên nhân:** `spring.jpa.hibernate.ddl-auto` không được set đúng

**Giải pháp:** Check `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update  # ← Phải là "update" cho Code First
spring.jpa.show-sql=true               # ← Để xem SQL statements
```

---

## ✅ Success Checklist

- [ ] SQL Server đang chạy (service status: Running)
- [ ] TCP/IP protocol enabled trong SQL Server Configuration Manager
- [ ] User `sa` với password `123` có thể login SSMS
- [ ] Database `MyDatabase` tồn tại (hoặc sẽ auto-create)
- [ ] IDE đã cài (VS Code / IntelliJ / Eclipse)
- [ ] Project được open trong IDE
- [ ] Click Run trong IDE → Application starts
- [ ] Console logs hiển thị Hibernate DDL statements
- [ ] SSMS query thành công: `USE MyDatabase; SELECT * FROM users;`

---

## 🎓 Next Steps sau khi Code First thành công

### 1. Thêm Sample Data

```sql
USE MyDatabase;
GO

-- Insert test users (password đã được BCrypt hash)
INSERT INTO users (user_name, hash_password) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
    ('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi');

-- Insert test products
INSERT INTO products (product_name, category, description, price, quantity) VALUES
    ('Laptop Dell XPS 13', 'Electronics', 'High-end ultrabook', 25000000, 10),
    ('The Great Gatsby', 'Books', 'Classic novel', 150000, 50),
    ('T-Shirt Nike', 'Clothing', 'Cotton t-shirt', 350000, 100);

-- Verify
SELECT * FROM users;
SELECT * FROM products;
```

### 2. Test REST API Endpoints

Application expose các endpoints sau:

```
POST   /api/auth/login          - Login và nhận JWT token
GET    /api/products             - List all products
GET    /api/products/{id}        - Get product by ID
POST   /api/products             - Create new product
PUT    /api/products/{id}        - Update product
DELETE /api/products/{id}        - Delete product
```

Test bằng Postman, cURL, hoặc Thunder Client (VS Code extension).

### 3. Run Unit Tests

```bash
cd "d:/software testing/backend"

# Chạy tất cả tests
./mvnw.cmd test

# Hoặc trong IDE: Right-click test class → Run Tests
```

### 4. Frontend Integration

Sau khi backend hoạt động, connect với frontend React/Vue:

```javascript
// Frontend call API
const response = await fetch("http://localhost:8080/api/products");
const products = await response.json();
```

---

## 📚 Further Reading

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate DDL Auto Modes](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#configurations-hbmddl)
- [Spring Boot Database Initialization](https://docs.spring.io/spring-boot/reference/howto/data-initialization.html)
- [SQL Server with Spring Boot](https://learn.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server)

---

## 💡 Pro Tips

1. **Development vs Production:**

   - Dev: `spring.jpa.hibernate.ddl-auto=update`
   - Prod: `spring.jpa.hibernate.ddl-auto=validate` + Flyway/Liquibase

2. **View formatted SQL:**

   ```properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

3. **Auto-reload on code changes:**

   - Add dependency: `spring-boot-devtools`
   - Enable "Build project automatically" in IDE

4. **Database visualization:**
   - IntelliJ: Database tool window (View → Tool Windows → Database)
   - VS Code: Install "SQL Server (mssql)" extension
   - DBeaver: Free universal database tool

---

**🎉 Chúc bạn thành công với Code First!**

Nếu gặp vấn đề, check các file hướng dẫn chi tiết ở trên hoặc xem logs để debug.
