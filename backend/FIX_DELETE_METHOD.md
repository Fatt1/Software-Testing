# Fix: deleteById() Method Parameter Type Mismatch

## Lỗi

```
D:\software testing\backend\src\test\java\com\flogin\ProductServiceTest.java:785:60
java: incompatible types: com.flogin.entity.Product cannot be converted to java.lang.Long
```

## Nguyên nhân

Sau khi refactor ProductService để sử dụng JPA, method `deleteById()` của JpaRepository chỉ nhận **Long id** làm parameter, không nhận Product object.

### ProductRepository interface (JpaRepository)

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository cung cấp:
    // void deleteById(Long id)  ✅
    // void delete(Product entity)  (method khác)
}
```

### ProductService.deleteProduct()

```java
public void deleteProduct(long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

    // ✅ Đúng: truyền Long id
    productRepository.deleteById(id);

    // ❌ Sai: truyền Product object
    // productRepository.deleteById(product);
}
```

## Lỗi trong Test

### ❌ Code cũ (sai):

```java
@Test
@DisplayName("TC41: Xóa sản phẩm thành công")
void testDeleteProduct_Success() {
    // Arrange
    Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    doNothing().when(productRepository).deleteById(product);  // ❌ Sai kiểu

    // Act
    productService.deleteProduct(1L);

    // Assert
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).deleteById(product);  // ❌ Sai kiểu
}
```

**Lỗi compile:**

- Line 785: `doNothing().when(productRepository).deleteById(product);`
  - Compiler: "Product cannot be converted to Long"
- Line 792: `verify(productRepository, times(1)).deleteById(product);`
  - Compiler: "Product cannot be converted to Long"

### ✅ Code mới (đúng):

```java
@Test
@DisplayName("TC41: Xóa sản phẩm thành công")
void testDeleteProduct_Success() {
    // Arrange
    Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    doNothing().when(productRepository).deleteById(1L);  // ✅ Truyền Long id

    // Act
    productService.deleteProduct(1L);

    // Assert
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).deleteById(1L);  // ✅ Truyền Long id
}
```

## Các thay đổi đã thực hiện

### File: ProductServiceTest.java

**Test case đã sửa:**

- TC41: `testDeleteProduct_Success()` (lines 779-793)

**Thay đổi cụ thể:**

1. Line 785: `deleteById(product)` → `deleteById(1L)`
2. Line 792: `deleteById(product)` → `deleteById(1L)`

## JpaRepository Methods Reference

```java
public interface JpaRepository<T, ID> extends ... {

    // Delete by ID
    void deleteById(ID id);  // ← Dùng cái này

    // Delete by entity
    void delete(T entity);

    // Delete multiple
    void deleteAll(Iterable<? extends T> entities);
    void deleteAll();
}
```

**Trong trường hợp của chúng ta:**

- `T` = `Product`
- `ID` = `Long`

Vì vậy:

- ✅ `deleteById(Long id)` - Xóa theo ID
- ✅ `delete(Product entity)` - Xóa theo entity

Trong ProductService, chúng ta chọn `deleteById(id)` vì đã có `id` từ parameter và đã verify product tồn tại qua `findById()`.

## Kết quả

✅ Không còn lỗi compile  
✅ Test cases vẫn giữ nguyên logic  
✅ Signature của mock methods khớp với JpaRepository

## Best Practice

Khi refactor từ custom repository sang JpaRepository:

1. **Kiểm tra method signatures** trong JpaRepository interface
2. **Cập nhật service layer** trước
3. **Cập nhật test mocks** sau để khớp với service
4. **Chạy tests** để verify behavior không thay đổi

### Quy tắc chung:

- `deleteById()` → nhận `ID` (Long, Integer, String, etc.)
- `delete()` → nhận `Entity` (Product, User, etc.)
- `deleteAllById()` → nhận `Iterable<ID>`
- `deleteAll()` → nhận `Iterable<Entity>` hoặc không có parameter
