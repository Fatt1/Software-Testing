# Chạy Application từ IDE (Recommended)

## ⚠️ Vấn đề với mvnw.cmd

Nếu gặp lỗi:

```bash
$ mvnw.cmd clean compile
bash: mvnw.cmd: command not found
```

Hoặc:

```
Files\Java\jdk-23""=="" was unexpected at this time.
```

→ Đây là vấn đề với Maven Wrapper trong bash shell + đường dẫn Java có khoảng trắng.

## ✅ Giải pháp: Chạy từ IDE

### Option 1: Visual Studio Code (Recommended) ⭐

#### Bước 1: Cài Extension

1. Mở VS Code
2. Cài các extensions:
   - **Extension Pack for Java** (Microsoft)
   - **Spring Boot Extension Pack** (VMware)

#### Bước 2: Mở Project

1. File → Open Folder
2. Chọn thư mục: `d:\software testing\backend`
3. VS Code sẽ tự động detect Spring Boot project

#### Bước 3: Chạy Application

1. Mở file: `src/main/java/com/flogin/FloginApplication.java`
2. Bạn sẽ thấy nút ▶️ **Run** và **Debug** phía trên method `main()`
3. Click **Run** hoặc nhấn `F5`

```java
package com.flogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FloginApplication {

    public static void main(String[] args) {  // ← Click "Run" here
        SpringApplication.run(FloginApplication.class, args);
    }
}
```

#### Bước 4: Xem Logs

- Logs xuất hiện trong tab **TERMINAL** ở dưới
- Tìm dòng: `Hibernate: create table users (...)`
- Nếu thấy → Tables đã được tạo! ✅

---

### Option 2: IntelliJ IDEA

#### Bước 1: Mở Project

1. File → Open
2. Chọn thư mục: `d:\software testing\backend`
3. IntelliJ sẽ auto-detect Maven project

#### Bước 2: Sync Maven Dependencies

1. Right-click vào `pom.xml` → Maven → Reload Project
2. Hoặc click icon 🔄 "Reload All Maven Projects" ở Maven tool window

#### Bước 3: Chạy Application

1. Mở file: `FloginApplication.java`
2. Click vào icon ▶️ màu xanh bên cạnh class name hoặc method `main()`
3. Chọn **Run 'FloginApplication'**

Hoặc dùng shortcut:

- Windows/Linux: `Shift + F10`
- Mac: `Ctrl + R`

#### Bước 4: Xem Logs

- Logs xuất hiện trong tab **Run** ở dưới
- Tìm các DDL statements của Hibernate

---

### Option 3: Eclipse (Spring Tool Suite)

#### Bước 1: Import Project

1. File → Import → Maven → Existing Maven Projects
2. Root Directory: `d:\software testing\backend`
3. Chọn `pom.xml` → Finish

#### Bước 2: Update Maven Project

1. Right-click vào project → Maven → Update Project
2. Check "Force Update of Snapshots/Releases"
3. Click OK

#### Bước 3: Chạy Application

1. Right-click vào `FloginApplication.java`
2. Run As → Spring Boot App

Hoặc:

1. Click vào project trong Project Explorer
2. Click icon ▶️ "Run" trong toolbar
3. Chọn "Spring Boot App"

---

## 🔍 Verify Tables được tạo

### Trong Console/Logs, tìm:

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

2025-11-12 10:30:45.123  INFO ... : Started FloginApplication in 3.456 seconds
```

✅ **Nếu thấy các dòng trên → Code First đã hoạt động!**

---

## 📊 Kiểm tra trong SQL Server

Sau khi application start thành công, mở **SQL Server Management Studio (SSMS)**:

```sql
-- Connect tới SQL Server (localhost, user: sa, password: 123)

USE MyDatabase;
GO

-- Xem danh sách tables
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
-- Expected: products, users

-- Xem data (sẽ rỗng ban đầu)
SELECT * FROM users;
SELECT * FROM products;

-- Xem cấu trúc
EXEC sp_help 'users';
EXEC sp_help 'products';
```

---

## 🐛 Troubleshooting IDE

### VS Code: "Java runtime could not be located"

**Giải pháp:**

1. Install Java Development Kit (JDK) 21 hoặc 23
2. Set Java path trong VS Code:
   - `Ctrl + ,` → Settings
   - Search: "java.home"
   - Set path tới JDK folder

### IntelliJ: "Project SDK is not defined"

**Giải pháp:**

1. File → Project Structure (Ctrl + Alt + Shift + S)
2. Project → Project SDK
3. Chọn JDK 21 hoặc 23
4. Apply → OK

### Eclipse: "Build path specifies execution environment"

**Giải pháp:**

1. Right-click project → Properties
2. Java Build Path → Libraries
3. Edit JRE System Library
4. Chọn "Workspace default JRE" hoặc JDK 21/23

### Application không start: "Port 8080 already in use"

**Giải pháp 1:** Đổi port trong `application.properties`:

```properties
server.port=8081
```

**Giải pháp 2:** Kill process đang dùng port 8080:

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

---

## 🚀 Alternative: Fix Maven Wrapper (Advanced)

Nếu muốn fix mvnw.cmd để chạy từ bash:

### Bước 1: Kiểm tra JAVA_HOME

```bash
echo $JAVA_HOME
# Nếu trống hoặc có khoảng trắng trong path → vấn đề!
```

### Bước 2: Set JAVA_HOME tạm thời

```bash
# Tìm Java path
where java
# Output: C:\Program Files\Java\jdk-23\bin\java.exe

# Set JAVA_HOME (không có khoảng trắng)
export JAVA_HOME="/c/Program Files/Java/jdk-23"
export PATH="$JAVA_HOME/bin:$PATH"
```

### Bước 3: Chạy lại Maven Wrapper

```bash
cd "d:/software testing/backend"
./mvnw.cmd spring-boot:run
```

**Lưu ý:** Cách này phức tạp và không recommended. Tốt nhất là chạy từ IDE.

---

## ✅ Checklist để chạy thành công

- [ ] Đã cài IDE (VS Code / IntelliJ / Eclipse)
- [ ] Đã cài Java Development Kit (JDK 21 hoặc 23)
- [ ] Project được import/open trong IDE
- [ ] Maven dependencies đã được download (check `pom.xml`)
- [ ] SQL Server đang chạy (Service: SQL Server)
- [ ] File `application.properties` có đúng connection string
- [ ] Click Run trong IDE → Application start
- [ ] Xem logs có Hibernate DDL statements
- [ ] Verify trong SSMS: tables `users` và `products` tồn tại

---

## 📝 Summary

| Method                  | Difficulty | Recommended                 |
| ----------------------- | ---------- | --------------------------- |
| **VS Code**             | Easy       | ✅ Yes (Best for beginners) |
| **IntelliJ IDEA**       | Easy       | ✅ Yes (Best for Java devs) |
| **Eclipse/STS**         | Easy       | ✅ Yes (Good for Spring)    |
| Command line (mvnw.cmd) | Hard       | ❌ No (Path issues)         |
| Global Maven            | Medium     | ⚠️ Only if installed        |

**Recommendation:** Sử dụng IDE để run application. Nhanh, dễ, và debug thuận tiện hơn!
