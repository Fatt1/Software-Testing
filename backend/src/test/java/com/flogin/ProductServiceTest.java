package com.flogin;

import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import com.flogin.service.ProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductService Unit Test với Bean Validation (Đã tinh gọn)
 *
 * Test scenarios:
 * - createProduct() với Bean Validation (Kiểm tra đầy đủ)
 * - getProduct()
 * - updateProduct() (Chỉ kiểm tra logic update và 1 ca validation)
 * - deleteProduct()
 * - getAll() với pagination
 */
@DisplayName("ProductService Unit Test - Bean Validation")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private Validator mockValidator;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, mockValidator);
    }

    // ========================================================================================
    // A) TEST CREATE (Bao gồm đầy đủ Validation)
    // ========================================================================================

    @Nested
    @DisplayName("Test createProduct() method")
    class CreateProductTests {

        @Test
        @DisplayName("TC1: Tạo sản phẩm thành công với dữ liệu hợp lệ")
        void testCreateProduct_Success() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Gaming laptop", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);

            // Mock validator trả về không có lỗi
            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals("Laptop", result.getProductName());
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("TC2: Tạo sản phẩm thất bại - Product Name null")
        void testCreateProduct_NullProductName() {
            // Arrange
            ProductDto productDto = new ProductDto(null, 15000.0, "Description", 10, "Electronics");

            // Mock validator trả về violation
            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Product Name không được rỗng"));
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC3: Tạo sản phẩm thất bại - Product Name rỗng")
        void testCreateProduct_EmptyProductName() {
            // Arrange
            ProductDto productDto = new ProductDto("", 15000.0, "Description", 10, "Electronics");

            // Mock validator trả về violation
            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Product Name không được rỗng"));
            verify(mockValidator, times(1)).validate(productDto);
        }

        @Test
        @DisplayName("TC4: Tạo sản phẩm thất bại - Product Name chỉ có khoảng trắng")
        void testCreateProduct_WhitespaceProductName() {
            // Arrange
            ProductDto productDto = new ProductDto("   ", 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC5: Tạo sản phẩm thất bại - Product Name quá ngắn (2 ký tự)")
        void testCreateProduct_ShortProductName() {
            // Arrange
            ProductDto productDto = new ProductDto("ab", 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name phải từ 3 đến 100 ký tự");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC6: Tạo sản phẩm thất bại - Product Name quá dài (101 ký tự)")
        void testCreateProduct_LongProductName() {
            // Arrange
            String longName = "a".repeat(101);
            ProductDto productDto = new ProductDto(longName, 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name phải từ 3 đến 100 ký tự");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC7: Tạo sản phẩm thất bại - Price null")
        void testCreateProduct_NullPrice() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop",  null, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Price không được để trống");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC8: Tạo sản phẩm thất bại - Price = 0")
        void testCreateProduct_ZeroPrice() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 0.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Price phải > 0");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC9: Tạo sản phẩm thất bại - Price âm")
        void testCreateProduct_NegativePrice() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", -100.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Price phải > 0");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }
        
        @Test
        @DisplayName("TC10: Tạo sản phẩm thất bại - Price vượt quá giới hạn")
        void testCreateProduct_PriceExceedsLimit() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 1000000000.0, "Description", 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Price không được vượt quá 999,999,999");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }
        
        @Test
        @DisplayName("TC11: Tạo sản phẩm thất bại - Quantity null")
        void testCreateProduct_NullQuantity() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", null, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Quantity không được để trống");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC12: Tạo sản phẩm thất bại - Quantity âm")
        void testCreateProduct_NegativeQuantity() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", -1, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Quantity phải >= 0");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC13: Tạo sản phẩm thất bại - Quantity vượt quá giới hạn")
        void testCreateProduct_QuantityExceedsLimit() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", 100000, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Quantity không được vượt quá 99,999");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
            
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC14: Tạo sản phẩm thất bại - Description quá dài")
        void testCreateProduct_LongDescription() {
            // Arrange
            String longDescription = "a".repeat(501);
            ProductDto productDto = new ProductDto("Laptop", 15000.0, longDescription, 10, "Electronics");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Description không được quá 500 ký tự");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC15: Tạo sản phẩm thất bại - Category null")
        void testCreateProduct_NullCategory() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", 10, null);

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Category không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC16: Tạo sản phẩm thất bại - Category rỗng")
        void testCreateProduct_EmptyCategory() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", 10, "");

            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Category không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        }

        @Test
        @DisplayName("TC17: Tạo sản phẩm thất bại - Category không hợp lệ (Business Logic)")
        void testCreateProduct_InvalidCategory() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, "Description", 10, "InvalidCategory");

            // Mock validator trả về không có lỗi (Bean Validation pass)
            when(mockValidator.validate(productDto)).thenReturn(Set.of());

            // Act & Assert
            // Lỗi này đến từ Category.isValid(), không phải Bean Validation
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Category"));
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC18: Tạo sản phẩm thành công - Tất cả categories hợp lệ")
        void testCreateProduct_AllValidCategories() {
            List<String> validCategories = Arrays.asList("Electronics", "Books", "Clothing", "Toys", "Groceries");

            for (String category : validCategories) {
                // Arrange
                ProductDto productDto = new ProductDto("Product", 100.0, "Description", 10, category);
                Product savedProduct = new Product(1L, category, "Description", 10, "Product", 100.0);
                when(mockValidator.validate(productDto)).thenReturn(Set.of());
                when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

                // Act
                ProductDto result = productService.createProduct(productDto);

                // Assert
                assertNotNull(result);
                assertEquals(category, result.getCategory());
            }
            verify(mockValidator, atLeastOnce()).validate(any(ProductDto.class));
        }

        @Test
        @DisplayName("TC19: Tạo sản phẩm thành công - Description null (optional field)")
        void testCreateProduct_NullDescription() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, null, 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", null, 10, "Laptop", 15000.0);

            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertNull(result.getDescription());
        }
        
        @Test
        @DisplayName("TC20: Tạo sản phẩm thất bại - Nhiều lỗi validation")
        void testCreateProduct_MultipleValidationErrors() {
            // Arrange
            ProductDto productDto = new ProductDto("ab", -100.0, null, -1, "Electronics");

            ConstraintViolation<ProductDto> violation1 = mock(ConstraintViolation.class);
            ConstraintViolation<ProductDto> violation2 = mock(ConstraintViolation.class);
            ConstraintViolation<ProductDto> violation3 = mock(ConstraintViolation.class);
            when(violation1.getMessage()).thenReturn("Product Name phải từ 3 đến 100 ký tự");
            when(violation2.getMessage()).thenReturn("Price phải > 0");
            when(violation3.getMessage()).thenReturn("Quantity phải >= 0");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation1, violation2, violation3));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(productDto)
            );

            String message = exception.getMessage();
            assertTrue(message.contains("Product Name") || message.contains("Price") || message.contains("Quantity"));
            verify(mockValidator, times(1)).validate(productDto);
        }

        @Test
        @DisplayName("TC21: Tạo sản phẩm thành công - Product Name 3 ký tự (boundary)")
        void testCreateProduct_ProductNameMin() {
            // Arrange
            ProductDto productDto = new ProductDto("abc", 100.0, "Description", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Description", 10, "abc", 100.0);

            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals("abc", result.getProductName());
        }

        @Test
        @DisplayName("TC22: Tạo sản phẩm thành công - Product Name 100 ký tự (boundary)")
        void testCreateProduct_ProductNameMax() {
            // Arrange
            String maxName = "a".repeat(100);
            ProductDto productDto = new ProductDto(maxName, 100.0, "Description", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Description", 10, maxName, 100.0);
            
            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getProductName().length());
        }
        
        @Test
        @DisplayName("TC23: Tạo sản phẩm thành công - Price với giá trị thập phân")
        void testCreateProduct_DecimalPrice() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15999.99, "Description", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Description", 10, "Laptop", 15999.99);

            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals(15999.99, result.getPrice(), 0.001);
        }
    }

    // ========================================================================================
    // B) TEST GET BY ID
    // ========================================================================================

    @Nested
    @DisplayName("Test getProductById() method")
    class GetProductTests {

        @Test
        @DisplayName("TC26: Lấy sản phẩm thành công theo ID")
        void testGetProductById_Success() {
            // Arrange
            Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            ProductDto result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Laptop", result.getProductName());
            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("TC27: Lấy sản phẩm thất bại - ID không tồn tại")
        void testGetProductById_NotFound() {
            // Arrange
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.getProductById(999L)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
            verify(productRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("TC28: Lấy sản phẩm với ID = 0")
        void testGetProductById_ZeroId() {
            // Arrange
            when(productRepository.findById(0L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> productService.getProductById(0L));
        }
    }

    // ========================================================================================
    // C) TEST UPDATE (Đã tinh gọn, chỉ test logic Update)
    // ========================================================================================

    @Nested
    @DisplayName("Test updateProduct() method")
    class UpdateProductTests {

        @Test
        @DisplayName("TC29: Cập nhật sản phẩm thành công")
        void testUpdateProduct_Success() {
            // Arrange
            ProductDto productDto = new ProductDto("Updated Laptop", 20000.0, "Updated description", 15, "Electronics");
            productDto.setId(1L);

            Product existingProduct = new Product(1L, "Electronics", "Old description", 10, "Laptop", 15000.0);
            Product updatedProduct = new Product(1L, "Electronics", "Updated description", 15, "Updated Laptop", 20000.0);

            when(mockValidator.validate(productDto)).thenReturn(Set.of()); // Giả sử validation thành công
            when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            // Act
            ProductDto result = productService.updateProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals("Updated Laptop", result.getProductName());
            assertEquals(20000.0, result.getPrice());
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("TC30: Cập nhật sản phẩm thất bại - ID không tồn tại")
        void testUpdateProduct_NotFound() {
            // Arrange
            ProductDto productDto = new ProductDto("Updated Laptop", 20000.0, "Description", 15, "Electronics");
            productDto.setId(999L);
            
            when(mockValidator.validate(productDto)).thenReturn(Set.of()); // Giả sử validation thành công
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.updateProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
            verify(mockValidator, times(1)).validate(productDto); // Validation được gọi trước khi tìm
            verify(productRepository, times(1)).findById(999L);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC31: Cập nhật sản phẩm thất bại - Lỗi Bean Validation (Test 1 ca)")
        void testUpdateProduct_BeanValidationFails() {
            // Arrange
            ProductDto productDto = new ProductDto(null, 20000.0, "Description", 15, "Electronics");
            productDto.setId(1L);

            // Mock validator trả về violation
            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name không được rỗng");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Product Name không được rỗng"));
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, never()).findById(anyLong());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC37: Cập nhật sản phẩm thất bại - Category không hợp lệ (Business Logic)")
        void testUpdateProduct_InvalidCategory() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 20000.0, "Description", 15, "InvalidCategory");
            productDto.setId(1L);

            // Mock validator trả về không có lỗi (Bean Validation pass)
            when(mockValidator.validate(productDto)).thenReturn(Set.of());

            // Act & Assert
            // Lỗi này đến từ Category.isValid(), không phải Bean Validation
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateProduct(productDto)
            );

            assertTrue(exception.getMessage().contains("Category"));
            verify(mockValidator, times(1)).validate(productDto);
            verify(productRepository, never()).findById(anyLong());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC39: Cập nhật sản phẩm thành công - Thay đổi category")
        void testUpdateProduct_ChangeCategory() {
            // Arrange
            ProductDto productDto = new ProductDto("Book Title", 50.0, "A great book", 100, "Books");
            productDto.setId(1L);

            Product existingProduct = new Product(1L, "Electronics", "Old description", 10, "Laptop", 15000.0);
            Product updatedProduct = new Product(1L, "Books", "A great book", 100, "Book Title", 50.0);

            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            // Act
            ProductDto result = productService.updateProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals("Books", result.getCategory());
        }
        
        @Test
        @DisplayName("TC40.2: Cập nhật sản phẩm thành công - Update description sang null")
        void testUpdateProduct_DescriptionToNull() {
            // Arrange
            ProductDto productDto = new ProductDto("Laptop", 15000.0, null, 10, "Electronics");
            productDto.setId(1L);

            Product existingProduct = new Product(1L, "Electronics", "Old description", 10, "Laptop", 15000.0);
            Product updatedProduct = new Product(1L, "Electronics", null, 10, "Laptop", 15000.0);

            when(mockValidator.validate(productDto)).thenReturn(Set.of());
            when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            // Act
            ProductDto result = productService.updateProduct(productDto);

            // Assert
            assertNotNull(result);
            assertNull(result.getDescription());
        }
    }

    // ========================================================================================
    // D) TEST DELETE
    // ========================================================================================

    @Nested
    @DisplayName("Test deleteProduct() method")
    class DeleteProductTests {

        @Test
        @DisplayName("TC41: Xóa sản phẩm thành công")
        void testDeleteProduct_Success() {
            // Arrange
            Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).deleteById(1L);

            // Act
            productService.deleteProduct(1L);

            // Assert
            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("TC42: Xóa sản phẩm thất bại - ID không tồn tại")
        void testDeleteProduct_NotFound() {
            // Arrange
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.deleteProduct(999L)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
            verify(productRepository, times(1)).findById(999L);
            verify(productRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("TC43: Xóa sản phẩm với ID = 0")
        void testDeleteProduct_ZeroId() {
            // Arrange
            when(productRepository.findById(0L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> productService.deleteProduct(0L));
        }

        @Test
        @DisplayName("TC44: Xóa nhiều sản phẩm liên tiếp")
        void testDeleteProduct_Multiple() {
            // Arrange
            Product product1 = new Product(1L, "Electronics", "Product 1", 10, "Laptop", 15000.0);
            Product product2 = new Product(2L, "Books", "Product 2", 20, "Book", 50.0);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
            when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
            doNothing().when(productRepository).deleteById(anyLong());

            // Act
            productService.deleteProduct(1L);
            productService.deleteProduct(2L);

            // Assert
            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).findById(2L);
            verify(productRepository, times(2)).deleteById(anyLong());
        }
    }

    // ========================================================================================
    // E) TEST GET ALL (PAGINATION)
    // ========================================================================================

    @Nested
    @DisplayName("Test getAll() method with pagination")
    class GetAllProductsTests {

        @Test
        @DisplayName("TC45: Lấy danh sách sản phẩm với pagination - Trang đầu tiên")
        void testGetAll_FirstPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = Arrays.asList(
                    new Product(1L, "Electronics", "Product 1", 10, "Laptop", 15000.0),
                    new Product(2L, "Books", "Product 2", 20, "Book", 50.0)
            );
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // Act
            Page<ProductDto> result = productService.getAll(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(0, result.getNumber());
            verify(productRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("TC46: Lấy danh sách sản phẩm - Trang rỗng")
        void testGetAll_EmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(productRepository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<ProductDto> result = productService.getAll(pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
            verify(productRepository, times(1)).findAll(pageable);
        }

        
    }
}