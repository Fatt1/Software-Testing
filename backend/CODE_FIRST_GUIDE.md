# Code First - Tạo Database từ Entities

## ✅ Những gì đã chuẩn bị sẵn

### 1. Entities với JPA Annotations

#### User.java

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String userName;

    @Column(name = "hash_password", nullable = false, length = 255)
    private String hashPassword;
}
```

#### Product.java

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;
}
```

### 2. application.properties đã config

```properties
# SQL Server Connection
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MyDatabase;createDatabaseIfNotExist=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123

# Hibernate Code First
spring.jpa.hibernate.ddl-auto=update  ← Quan trọng!
spring.jpa.show-sql=true              ← Hiển thị SQL
spring.jpa.properties.hibernate.format_sql=true
```

## 📋 Các bước thực hiện

### Bước 1: Kiểm tra SQL Server

**Mở SQL Server Configuration Manager:**

1. Tìm `SQL Server (MSSQLSERVER)` hoặc instance name của bạn
2. Đảm bảo status là **Running**
3. Nếu chưa chạy: Right-click → Start

**Kiểm tra TCP/IP enabled:**

1. SQL Server Configuration Manager → SQL Server Network Configuration
2. Protocols for MSSQLSERVER → TCP/IP → **Enabled**
3. Nếu vừa enable, phải restart SQL Server service

### Bước 2: Tạo Database (Optional - có thể tự động)

**Cách 1: Tự động** ✅ Recommended

- Connection string đã có `createDatabaseIfNotExist=true`
- Database sẽ tự động tạo khi chạy app

**Cách 2: Thủ công**

```sql
-- Mở SQL Server Management Studio (SSMS)
-- Connect với user 'sa'
-- Run query:

CREATE DATABASE MyDatabase;
GO

USE MyDatabase;
GO
```

### Bước 3: Chạy Application

**Option A: Dùng script tự động** ⭐ Recommended

Double-click file: `run-app.bat`

**Option B: Command line**

```bash
# Từ thư mục backend
cd "d:/software testing/backend"

# ⚠️ Nếu gặp lỗi "command not found" hoặc "unexpected at this time"
# → Xem file RUN_FROM_IDE.md để chạy từ IDE (recommended)

# Clean và compile
./mvnw.cmd clean compile

# Chạy application
./mvnw.cmd spring-boot:run
```

**Note:** Nếu mvnw.cmd không hoạt động trong bash shell, hãy **chạy từ IDE** (xem `RUN_FROM_IDE.md`)

**Option C: IDE (IntelliJ IDEA / VS Code)**

1. Mở file `FloginApplication.java`
2. Click vào nút ▶️ Run bên cạnh `main()` method
3. Hoặc Right-click → Run 'FloginApplication'

### Bước 4: Xem logs để verify

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

2025-11-12 ... Started FloginApplication in 3.456 seconds
```

✅ **Nếu thấy các DDL statements → Tables đã được tạo thành công!**

### Bước 5: Verify trong SQL Server

**Mở SSMS và chạy:**

```sql
-- Connect tới SQL Server
USE MyDatabase;
GO

-- Xem danh sách tables
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
-- Kết quả: products, users

-- Kiểm tra cấu trúc table users
EXEC sp_help 'users';

-- Kiểm tra cấu trúc table products
EXEC sp_help 'products';

-- Hoặc dùng query này:
SELECT
    c.TABLE_NAME,
    c.COLUMN_NAME,
    c.DATA_TYPE,
    c.CHARACTER_MAXIMUM_LENGTH,
    c.IS_NULLABLE,
    CASE WHEN pk.COLUMN_NAME IS NOT NULL THEN 'PK' ELSE '' END AS [KEY]
FROM INFORMATION_SCHEMA.COLUMNS c
LEFT JOIN (
    SELECT ku.TABLE_NAME, ku.COLUMN_NAME
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS tc
    INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS ku
        ON tc.CONSTRAINT_TYPE = 'PRIMARY KEY'
        AND tc.CONSTRAINT_NAME = ku.CONSTRAINT_NAME
) pk ON c.TABLE_NAME = pk.TABLE_NAME AND c.COLUMN_NAME = pk.COLUMN_NAME
WHERE c.TABLE_NAME IN ('users', 'products')
ORDER BY c.TABLE_NAME, c.ORDINAL_POSITION;
```

## 📊 Kết quả mong đợi

### Table: users

```
Column          Type          Nullable  Key  Constraints
-----------------------------------------------------------
id              bigint        NO        PK   IDENTITY(1,1)
user_name       varchar(50)   NO             UNIQUE
hash_password   varchar(255)  NO
```

### Table: products

```
Column          Type          Nullable  Key  Constraints
-----------------------------------------------------------
id              bigint        NO        PK   IDENTITY(1,1)
category        varchar(50)   NO
description     varchar(500)  YES
price           float         NO
product_name    varchar(100)  NO
quantity        int           NO
```

## 🔧 Hiểu về `spring.jpa.hibernate.ddl-auto`

| Giá trị       | Hành động                           | Khi nào dùng                  |
| ------------- | ----------------------------------- | ----------------------------- |
| `create`      | Xóa và tạo lại schema mỗi lần start | ⚠️ Testing (mất data!)        |
| `create-drop` | Tạo khi start, xóa khi stop         | ⚠️ Testing (mất data!)        |
| `update`      | Cập nhật schema nếu có thay đổi     | ✅ **Development**            |
| `validate`    | Chỉ kiểm tra, không thay đổi        | ✅ **Production**             |
| `none`        | Không làm gì                        | Production (manual migration) |

**Recommended:**

- Development: `update`
- Production: `validate` + Flyway/Liquibase

## 🎯 Test Code First đang hoạt động

### Test 1: Thêm column mới vào Product

```java
// Thêm vào Product.java
@Column(name = "manufacturer", length = 100)
private String manufacturer;

// Thêm getter/setter
public String getManufacturer() { return manufacturer; }
public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
```

**Restart application** → Check logs:

```
Hibernate:
    alter table products
       add manufacturer varchar(100)
```

✅ Column mới được tự động thêm!

### Test 2: Thay đổi constraint

```java
// Đổi length của description từ 500 → 1000
@Column(name = "description", length = 1000)
private String description;
```

**Restart application** → Hibernate sẽ update column definition

⚠️ **Lưu ý:** Hibernate `update` không tự động:

- Xóa columns (phải xóa thủ công)
- Thay đổi column type (có thể lỗi nếu có data)
- Xóa tables

## 🐛 Troubleshooting

### Lỗi 1: Connection failed

```
Caused by: java.sql.SQLException: Cannot create PoolableConnectionFactory
```

**Giải pháp:**

1. Check SQL Server đang chạy: Services → SQL Server (MSSQLSERVER)
2. Verify port 1433: `netstat -an | findstr 1433`
3. Check firewall không block port 1433

### Lỗi 2: Login failed for user 'sa'

```
Login failed for user 'sa'
```

**Giải pháp:**

1. Verify password đúng trong application.properties
2. Enable SQL Server Authentication:
   - SSMS → Right-click server → Properties
   - Security → SQL Server and Windows Authentication mode
   - Restart SQL Server service

### Lỗi 3: Tables không được tạo

```
No DDL statements in logs
```

**Giải pháp:**

1. Check `spring.jpa.hibernate.ddl-auto=update` (không phải `none` hay `validate`)
2. Verify entities có `@Entity` annotation
3. Check entities trong package `com.flogin.*`
4. Xem log có error: `Error creating bean`

### Lỗi 4: Java version mismatch

```
The TCP/IP connection to the host localhost, port 1433 has failed
```

**Nếu dùng Java 23 nhưng project cần Java 21:**

- Cài Java 21 từ: https://adoptium.net/
- Set JAVA_HOME environment variable
- Hoặc đổi `java.version` trong pom.xml thành `23`

## ✅ Checklist hoàn thành

- [ ] SQL Server đang chạy (Services → SQL Server)
- [ ] TCP/IP protocol enabled
- [ ] Database `MyDatabase` tồn tại (hoặc sẽ auto-create)
- [ ] User `sa` với password `123` có quyền truy cập
- [ ] `spring.jpa.hibernate.ddl-auto=update` trong properties
- [ ] Application start thành công
- [ ] Thấy Hibernate DDL statements trong logs
- [ ] Tables `users` và `products` xuất hiện trong SSMS
- [ ] Có thể query: `SELECT * FROM users; SELECT * FROM products;`

## 🚀 Next Steps sau khi Code First thành công

1. **Thêm sample data:**

   ```sql
   INSERT INTO users (user_name, hash_password) VALUES ('admin', '$2a$10$...');
   INSERT INTO products (product_name, category, price, quantity, description)
   VALUES ('Laptop', 'Electronics', 25000000, 10, 'Gaming laptop');
   ```

2. **Test REST API endpoints:**

   - POST `/api/auth/login` - Authentication
   - GET `/api/products` - List products
   - POST `/api/products` - Create product

3. **Chạy unit tests:**

   ```bash
   mvnw.cmd test
   ```

4. **Integration testing với database:**

   - Tạo `@SpringBootTest` với real database
   - Test CRUD operations end-to-end

5. **Cân nhắc migration tool cho production:**
   - Flyway
   - Liquibase
