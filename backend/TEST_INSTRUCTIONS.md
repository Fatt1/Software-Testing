# Hướng dẫn chạy Unit Tests cho Backend

## 1. AuthService Tests

### Mô tả

File test: `src/test/java/com/flogin/AuthServiceTest.java`

**Coverage yêu cầu:** >= 85%

### Test Scenarios

#### A) Test method authenticate() (3 điểm)

- ✅ TC1-TC12: Login thành công, username không tồn tại, password sai, và các validation errors

#### B) Test validation methods riêng lẻ (1 điểm)

- ✅ Username validation: null, empty, whitespace, quá ngắn/dài, ký tự không hợp lệ
- ✅ Password validation: null, empty, quá ngắn/dài, thiếu chữ cái, thiếu số
- ✅ Multiple errors validation

### Chạy Tests

```bash
# Chạy tất cả tests cho AuthService
./mvnw test -Dtest=AuthServiceTest

# Chạy với coverage report
./mvnw test jacoco:report -Dtest=AuthServiceTest
```

---

## 2. ProductService Tests

### Mô tả

File test: `src/test/java/com/flogin/ProductServiceTest.java`

**Coverage yêu cầu:** >= 85%

### Validation Rules cho Product

- **Product Name:** 3-100 ký tự, không được rỗng
- **Price:** > 0, <= 999,999,999
- **Quantity:** >= 0, <= 99,999
- **Description:** <= 500 ký tự (optional)
- **Category:** Phải thuộc danh sách: `Electronics`, `Books`, `Clothing`, `Toys`, `Groceries`

### Test Scenarios

#### A) Test CRUD operations (4 điểm)

##### 1. Test createProduct()

- ✅ TC1: Tạo sản phẩm thành công
- ✅ TC2-TC17: Validation errors (Name, Price, Quantity, Description, Category)
- ✅ TC18: Tất cả categories hợp lệ
- ✅ TC19-TC24: Boundary values
- ✅ TC25: Multiple validation errors

##### 2. Test getProduct()

- ✅ TC26: Lấy sản phẩm thành công
- ✅ TC27-TC28: Product không tồn tại

##### 3. Test updateProduct()

- ✅ TC29: Cập nhật sản phẩm thành công
- ✅ TC30: Product không tồn tại
- ✅ TC31-TC38: Validation errors
- ✅ TC39-TC40: Cập nhật từng field

##### 4. Test deleteProduct()

- ✅ TC41: Xóa sản phẩm thành công
- ✅ TC42-TC44: Product không tồn tại, xóa nhiều products

##### 5. Test getAll() với pagination

- ✅ TC45: Trang đầu tiên
- ✅ TC46: Trang rỗng
- ✅ TC47: Trang thứ 2
- ✅ TC48: Kích thước trang khác nhau
- ✅ TC49-TC50: Single product, verify mapping

#### B) Edge Cases & Boundary Tests

- ✅ TC51-TC55: Boundary values, whitespace handling, decimal values

**Tổng số test cases: 55**

### Chạy Tests

```bash
# Chạy tất cả tests cho ProductService
./mvnw test -Dtest=ProductServiceTest

# Chạy với coverage report
./mvnw test jacoco:report -Dtest=ProductServiceTest
```

---

## 3. Chạy tất cả Tests

```bash
# Chạy tất cả unit tests
./mvnw clean test

# Chạy tests với coverage report
./mvnw clean test jacoco:report

# Xem coverage report (sau khi chạy lệnh trên)
# File: target/site/jacoco/index.html
```

---

## 4. Cấu trúc Annotations đã sử dụng

### LoginRequest.java

```java
@NotBlank(message = "Username không được để trống")
@Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
@Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username chỉ chứa chữ, số, và ký tự (-, ., _)")
private String userName;

@NotBlank(message = "Password không được để trống")
@Size(min = 6, max = 100, message = "Password phải từ 6 đến 100 ký tự")
@Pattern(regexp = ".*[a-zA-Z]+.*", message = "Password phải chứa ít nhất 1 chữ cái")
@Pattern(regexp = ".*[0-9]+.*", message = "Password phải chứa ít nhất 1 chữ số")
private String password;
```

### ProductDto.java

```java
@NotBlank(message = "Product Name không được rỗng")
@Size(min = 3, max = 100, message = "Product Name phải từ 3 đến 100 ký tự")
private String productName;

@NotNull(message = "Price không được để trống")
@DecimalMin(value = "0.01", message = "Price phải > 0")
@DecimalMax(value = "999999999", message = "Price không được vượt quá 999,999,999")
private double price;

@NotNull(message = "Quantity không được để trống")
@Min(value = 0, message = "Quantity phải >= 0")
@Max(value = 99999, message = "Quantity không được vượt quá 99,999")
private Integer quantity;

@Size(max = 500, message = "Description không được quá 500 ký tự")
private String description;

@NotBlank(message = "Category không được rỗng")
private String category;
```

---

## 5. Cài đặt JaCoCo cho Coverage Report

Thêm vào `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>jacoco-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>CLASS</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.85</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 6. Kết quả mong đợi

### AuthService

- ✅ Tất cả scenarios cho authenticate()
- ✅ Validation cho username và password
- ✅ Coverage >= 85%

### ProductService

- ✅ CRUD operations đầy đủ
- ✅ Validation cho tất cả fields
- ✅ Pagination tests
- ✅ Edge cases và boundary tests
- ✅ Coverage >= 85%

---

## 7. Lưu ý khi viết tests với Spring Boot + JPA

### Repository Pattern với Mockito

```java
@Mock
private ProductRepository productRepository;

@InjectMocks
private ProductService productService;
```

### Test Validation ở tầng Service

- Service layer thực hiện validation thủ công (không dùng `@Valid`)
- Annotations chỉ mang tính documentation
- Validation logic nằm trong method `validate()` của service

### Mock Repository responses

```java
// Mock save()
when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

// Mock findById()
when(productRepository.findById(1L)).thenReturn(Optional.of(product));

// Mock findAll() với pagination
when(productRepository.findAll(pageable)).thenReturn(productPage);

// Mock delete()
doNothing().when(productRepository).deleteById(product);
```

### Verify interactions

```java
verify(productRepository, times(1)).save(any(Product.class));
verify(productRepository, never()).save(any());
```

---

## 8. Troubleshooting

### Lỗi: JAVA_HOME not defined

```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

### Lỗi: Tests không chạy

```bash
# Clean và rebuild
./mvnw clean install

# Bỏ qua tests trước, sau đó chạy lại
./mvnw clean install -DskipTests
./mvnw test
```

### Kiểm tra version Maven và Java

```bash
./mvnw --version
java -version
```
