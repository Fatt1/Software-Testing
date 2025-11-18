# Hướng Dẫn Cập Nhật ProductServiceTest

## Tình Trạng Hiện Tại

ProductServiceTest đang được cập nhật để sử dụng Bean Validation annotations thay vì validation thủ công.

### Đã Hoàn Thành

- ✅ TC1: Tạo sản phẩm thành công (đã thêm mock validator)
- ✅ TC2: Product Name null (đã thêm mock validator)
- ✅ TC3: Product Name rỗng (đã thêm mock validator)
- ✅ TC4: Product Name chỉ có khoảng trắng (đã thêm mock validator)
- ✅ TC5: Product Name quá ngắn (đã thêm mock validator)
- ✅ TC6: Product Name quá dài (đã thêm mock validator)
- ✅ TC7: Price null (đã thêm mock validator)
- ✅ TC8: Price = 0 (đã thêm mock validator)

### Chưa Hoàn Thành

- ⏳ TC9: Price âm
- ⏳ TC10: Price vượt quá giới hạn
- ⏳ TC11-TC25: Các test cases còn lại trong CreateProductTests
- ⏳ UpdateProductTests: 12 test cases
- ⏳ GetProductTests: 3 test cases
- ⏳ DeleteProductTests: 4 test cases
- ⏳ GetAllProductsTests: 6 test cases
- ⏳ EdgeCasesTests: 5 test cases

## Pattern Cần Áp Dụng

### 1. Test Cases THÀNH CÔNG (Validation Pass)

```java
// Mock validator trả về không có lỗi
when(mockValidator.validate(productDto)).thenReturn(Set.of());

// Verify validator được gọi
verify(mockValidator, times(1)).validate(productDto);
```

### 2. Test Cases THẤT BẠI (Validation Fail)

```java
// Mock validator trả về violation
ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
when(violation.getMessage()).thenReturn("Error message here");
when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

// Verify validator được gọi và repository không được gọi
verify(mockValidator, times(1)).validate(productDto);
verify(productRepository, never()).save(any());
```

### 3. Test Cases KHÔNG CẦN Validator Mock

- GetProductById tests (chỉ đọc dữ liệu)
- DeleteProduct tests (không validate)
- GetAllProducts tests (không validate)

## Các Validation Messages Chuẩn

```java
// Product Name
"Product Name không được rỗng"
"Product Name phải từ 3 đến 100 ký tự"

// Price
"Price không được để trống"
"Price phải > 0"
"Price không được vượt quá 999999999"

// Quantity
"Quantity không được để trống"
"Quantity phải >= 0"
"Quantity không được vượt quá 99999"

// Description
"Description không được vượt quá 500 ký tự"

// Category
"Category không được rỗng"
"Category không hợp lệ"
```

## Các Bước Tiếp Theo

1. **Cập nhật TC9-TC25 trong CreateProductTests**

   - Thêm mock validator cho các test validation fail
   - Thêm verify mockValidator cho các test thành công

2. **Cập nhật UpdateProductTests**

   - Pattern tương tự CreateProductTests
   - Mock validator cho updateProduct()

3. **Kiểm tra GetProductTests, DeleteProductTests, GetAllProductsTests**

   - Các tests này không cần validator mock (không có validation logic)
   - Chỉ cần kiểm tra xem có cần thêm gì không

4. **Run tests để verify**

   ```bash
   ./mvnw.cmd test -Dtest=ProductServiceTest
   ```

5. **Kiểm tra coverage**
   ```bash
   ./mvnw.cmd test jacoco:report
   ```

## Lưu Ý Quan Trọng

- ⚠️ **Category validation**: Vẫn sử dụng `Category.isValid()` ngoài Bean Validation
- ⚠️ **Constructor injection**: ProductService nhận Validator qua constructor
- ⚠️ **Mock setup**: @Mock Validator mockValidator trong @BeforeEach
- ⚠️ **Không dùng @InjectMocks**: Sử dụng manual instantiation thay vì @InjectMocks

## Tham Khảo

Xem `AuthServiceTest.java` để tham khảo pattern hoàn chỉnh của Bean Validation testing.
