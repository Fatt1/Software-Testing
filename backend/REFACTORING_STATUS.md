# ProductServiceTest - Bean Validation Refactoring Summary

## ✅ Đã Hoàn Thành

### Setup & Configuration

- ✅ Added `@Mock Validator mockValidator`
- ✅ Updated `@BeforeEach setUp()` to instantiate with validator: `new ProductService(productRepository, mockValidator)`
- ✅ Removed `@InjectMocks` annotation
- ✅ Added imports for `ConstraintViolation` and `Validator`

### Test Cases Updated (8/55)

- ✅ TC1: Create product success - Added `when(mockValidator.validate(productDto)).thenReturn(Set.of())`
- ✅ TC2: Product Name null - Added mock ConstraintViolation
- ✅ TC3: Product Name empty - Added mock ConstraintViolation
- ✅ TC4: Product Name whitespace - Added mock ConstraintViolation
- ✅ TC5: Product Name too short - Added mock ConstraintViolation
- ✅ TC6: Product Name too long - Added mock ConstraintViolation
- ✅ TC7: Price null - Added mock ConstraintViolation
- ✅ TC8: Price zero - Added mock ConstraintViolation
- ✅ TC9: Price negative - Added mock ConstraintViolation

## ⏳ Cần Cập Nhật (47 test cases)

### CreateProductTests (còn 16 tests)

**TC10: Price exceeds limit**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Price không được vượt quá 999,999,999");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC11: Quantity null**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Quantity không được để trống");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC12: Quantity negative**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Quantity phải >= 0");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC13: Quantity exceeds limit**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Quantity không được vượt quá 99,999");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC14: Description too long**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Description không được quá 500 ký tự");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC15: Category null**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Category không được rỗng");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC16: Category empty**

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Category không được rỗng");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
verify(mockValidator, times(1)).validate(productDto);
```

**TC17: Category invalid - SPECIAL CASE**

```java
// Không mock validator vì lỗi này từ Category.isValid(), không phải Bean Validation
// Giữ nguyên test này
```

**TC18: All valid categories - SUCCESS CASES**

```java
// Thêm vào mỗi success test trong loop:
when(mockValidator.validate(productDto)).thenReturn(Set.of());
// Và sau assert:
verify(mockValidator, times(1)).validate(productDto);
```

**TC19-TC25: Boundary tests - SUCCESS CASES**

```java
// Tất cả đều là success cases, thêm:
when(mockValidator.validate(productDto)).thenReturn(Set.of());
verify(mockValidator, times(1)).validate(productDto);
```

### UpdateProductTests (12 tests)

**Tất cả tests trong UpdateProductTests tương tự CreateProductTests:**

- Success cases: Mock `when(mockValidator.validate(productDto)).thenReturn(Set.of())`
- Validation fail cases: Mock ConstraintViolation tương ứng
- Exception from repository: Không cần mock validator

### GetProductTests (3 tests) - ✅ KHÔNG CẦN CẬP NHẬT

- ✅ TC1: Get existing product - Không validation
- ✅ TC2: Get non-existing product - Không validation
- ✅ TC3: Get with null ID - Không validation

### DeleteProductTests (4 tests) - ✅ KHÔNG CẦN CẬP NHẬT

- ✅ TC1: Delete existing product - Không validation
- ✅ TC2: Delete non-existing product - Không validation
- ✅ TC3: Delete with null ID - Không validation
- ✅ TC4: Delete with exception - Không validation

### GetAllProductsTests (6 tests) - ✅ KHÔNG CẦN CẬP NHẬT

- ✅ All tests - Không validation

### EdgeCasesTests (5 tests)

- ⏳ Kiểm tra từng test xem có cần mock validator không
- Nếu test gọi createProduct/updateProduct thì cần mock
- Nếu chỉ test repository behavior thì không cần

## Cách Áp Dụng Nhanh

### 1. Tìm tất cả validation fail tests

```bash
grep -n "IllegalArgumentException exception = assertThrows" ProductServiceTest.java | grep -v "mockValidator"
```

### 2. Cho mỗi test tìm được, thêm 3 dòng sau "// Arrange":

```java
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("[ERROR_MESSAGE]");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
```

### 3. Thêm verify trước verify(productRepository, never()):

```java
verify(mockValidator, times(1)).validate(productDto);
```

### 4. Tìm tất cả success tests trong CreateProduct và UpdateProduct:

```bash
grep -n "assertNotNull(result)" ProductServiceTest.java | grep -v "mockValidator"
```

### 5. Cho mỗi success test, thêm sau phần khai báo data:

```java
when(mockValidator.validate(productDto)).thenReturn(Set.of());
```

### 6. Thêm verify sau assertions:

```java
verify(mockValidator, times(1)).validate(productDto);
```

## Validation Messages Reference

| Scenario                | Message                                                              |
| ----------------------- | -------------------------------------------------------------------- |
| Product Name null/empty | "Product Name không được rỗng"                                       |
| Product Name length     | "Product Name phải từ 3 đến 100 ký tự"                               |
| Price null              | "Price không được để trống"                                          |
| Price ≤ 0               | "Price phải > 0"                                                     |
| Price > 999999999       | "Price không được vượt quá 999,999,999"                              |
| Quantity null           | "Quantity không được để trống"                                       |
| Quantity < 0            | "Quantity phải >= 0"                                                 |
| Quantity > 99999        | "Quantity không được vượt quá 99,999"                                |
| Description > 500       | "Description không được quá 500 ký tự"                               |
| Category null/empty     | "Category không được rỗng"                                           |
| Category invalid        | "Category không hợp lệ" (from Category.isValid, not Bean Validation) |

## Lưu Ý Đặc Biệt

1. **Category validation**: Test TC17 (invalid category) KHÔNG sử dụng Bean Validation vì lỗi từ `Category.isValid()`, không phải từ annotation. Giữ nguyên test này.

2. **Constructor injection**: ProductService constructor nhận (ProductRepository, Validator), đảm bảo @BeforeEach đã correct.

3. **Import statements**: Đã thêm đầy đủ imports cho ConstraintViolation và Validator.

4. **Times(1)**: Mỗi validator call chỉ được gọi 1 lần duy nhất.

5. **Success vs Fail**:
   - Success: `thenReturn(Set.of())` - empty set
   - Fail: `thenReturn(Set.of(violation))` - set with violations

## Testing After Changes

```bash
cd backend
./mvnw.cmd test -Dtest=ProductServiceTest
```

hoặc run từ IDE (IntelliJ IDEA/Eclipse).

## Expected Results

- All 55 tests should pass
- No compilation errors
- Validator is called appropriately in each test
- ProductService now uses Bean Validation consistently
