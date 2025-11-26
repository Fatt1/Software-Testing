package com.flogin;

import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
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
 * ProductService Unit Test - Với Boundary, Edge Cases và Negative Tests
 */
@DisplayName("ProductService Unit Test")
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
    // A) TEST CREATE - Bao gồm Boundary, Edge Cases và Negative Tests
    // ========================================================================================

    @Nested
    @DisplayName("Test createProduct() method - Simplified")
    class CreateProductTests {

        // ========== POSITIVE TESTS ==========

        @Test
        @DisplayName("TC1: Tạo sản phẩm thành công - Happy path")
        void testCreateProduct_Success() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Gaming laptop", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(request);

            // Assert
            assertNotNull(result);
            assertEquals("Laptop", result.getProductName());
            assertEquals(15000.0, result.getPrice());
            verify(mockValidator, times(1)).validate(request);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        // ========== BOUNDARY TESTS ==========

        @Test
        @DisplayName("TC4: Product Name - Below min boundary (2 ký tự)")
        void testCreateProduct_ProductName_BelowMinBoundary() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("AB", 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<CreateProductRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name phải từ 3 đến 100 ký tự");
            when(mockValidator.validate(request)).thenReturn(Set.of(violation));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(request)
            );

            assertTrue(exception.getMessage().contains("Product Name phải từ 3 đến 100 ký tự"));
        }


        @Test
        @DisplayName("TC6: Product Name - Above max boundary (101 ký tự)")
        void testCreateProduct_ProductName_AboveMaxBoundary() {
            // Arrange
            String productName = "A".repeat(101); // 101 ký tự
            CreateProductRequest request = new CreateProductRequest(productName, 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<CreateProductRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name phải từ 3 đến 100 ký tự");
            when(mockValidator.validate(request)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(request));
        }




        @Test
        @DisplayName("TC10: Quantity - Invalid (-1)")
        void testCreateProduct_Quantity_Negative() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Description", -1, "Electronics");

            ConstraintViolation<CreateProductRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Quantity phải >= 0");
            when(mockValidator.validate(request)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(request));
        }

        // ========== NEGATIVE TESTS ==========

        @Test
        @DisplayName("TC11: Product Name null hoặc rỗng")
        void testCreateProduct_ProductName_NullOrEmpty() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest(null, 15000.0, "Description", 10, "Electronics");

            ConstraintViolation<CreateProductRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Product Name không được rỗng");
            when(mockValidator.validate(request)).thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(request));
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC12: Category không hợp lệ")
        void testCreateProduct_Category_Invalid() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Description", 10, "InvalidCategory");

            when(mockValidator.validate(request)).thenReturn(Set.of());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(request)
            );

            assertTrue(exception.getMessage().contains("Category"));
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC14: Category null - Validation fail trước")
        void testCreateProduct_Category_Null() {
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Description", 10, null);

            // @NotBlank phải catch null trước → Validation fail
            ConstraintViolation<CreateProductRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Category không được rỗng");
            when(mockValidator.validate(request)).thenReturn(Set.of(violation));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(request)
            );

            assertTrue(exception.getMessage().contains("Validation failed"));
        }


         @Test
        @DisplayName("TC18: Quantity - Số lượng lớn")
        void testCreateProduct_Quantity_LargeNumber() {
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Description", Integer.MAX_VALUE, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Description", Integer.MAX_VALUE, "Laptop", 15000.0);

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductDto result = productService.createProduct(request);

            assertEquals(Integer.MAX_VALUE, result.getQuantity());
        }

        @Test
        @DisplayName("TC21: Tạo sản phẩm thất bại - Tên sản phẩm đã tồn tại")
        void testCreateProduct_DuplicateProductName() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("Laptop", 15000.0, "Gaming laptop", 10, "Electronics");

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.existsByProductName("Laptop")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(request)
            );

            assertTrue(exception.getMessage().contains("Product name 'Laptop' đã tồn tại"));
            verify(productRepository, times(1)).existsByProductName("Laptop");
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC22: Tạo sản phẩm thành công - Tên sản phẩm chưa tồn tại")
        void testCreateProduct_UniqueProductName() {
            // Arrange
            CreateProductRequest request = new CreateProductRequest("New Laptop", 15000.0, "Gaming laptop", 10, "Electronics");
            Product savedProduct = new Product(1L, "Electronics", "Gaming laptop", 10, "New Laptop", 15000.0);

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.existsByProductName("New Laptop")).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            ProductDto result = productService.createProduct(request);

            // Assert
            assertNotNull(result);
            assertEquals("New Laptop", result.getProductName());
            verify(productRepository, times(1)).existsByProductName("New Laptop");
            verify(productRepository, times(1)).save(any(Product.class));
        }

    }

    // ========================================================================================
    // B) TEST GET BY ID
    // ========================================================================================

    @Nested
    @DisplayName("Test getProductById() method")
    class GetProductTests {

        @Test
        @DisplayName("TC35: Lấy sản phẩm thành công theo ID")
        void testGetProductById_Success() {
            Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDto result = productService.getProductById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Laptop", result.getProductName());
            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("TC36: Lấy sản phẩm thất bại - ID không tồn tại")
        void testGetProductById_NotFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.getProductById(999L)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
            verify(productRepository, times(1)).findById(999L);
        }
    }

    // ========================================================================================
    // C) TEST UPDATE
    // ========================================================================================

    @Nested
    @DisplayName("Test updateProduct() method")
    class UpdateProductTests {

        @Test
        @DisplayName("TC37: Cập nhật sản phẩm thành công")
        void testUpdateProduct_Success() {
            UpdateProductRequest request = new UpdateProductRequest("Updated Laptop", 20000.0, "Updated description", 15, "Electronics");
            Product existingProduct = new Product(1L, "Electronics", "Old description", 10, "Laptop", 15000.0);
            Product updatedProduct = new Product(1L, "Electronics", "Updated description", 15, "Updated Laptop", 20000.0);

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            ProductDto result = productService.updateProduct(1L, request);

            assertNotNull(result);
            assertEquals("Updated Laptop", result.getProductName());
            assertEquals(20000.0, result.getPrice());
        }

        @Test
        @DisplayName("TC39: Update - Validation fail với nhiều lỗi")
        void testUpdateProduct_MultipleValidationErrors() {
            // Arrange
            UpdateProductRequest request = new UpdateProductRequest(null, 0.0, null, -1, null);


            ConstraintViolation<UpdateProductRequest> violation1 = mock(ConstraintViolation.class);
            when(violation1.getMessage()).thenReturn("Product Name không được rỗng");

            ConstraintViolation<UpdateProductRequest> violation2 = mock(ConstraintViolation.class);
            when(violation2.getMessage()).thenReturn("Price phải > 0");

            when(mockValidator.validate(request)).thenReturn(Set.of(violation1, violation2));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, request));
            verify(productRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("TC40: Update - Category không hợp lệ")
        void testUpdateProduct_InvalidCategory() {
            // Arrange
            UpdateProductRequest request = new UpdateProductRequest("Laptop", 15000.0, "Description", 10, "InvalidCategory");
            Product existingProduct = new Product(1L, "Electronics", "Old description", 10, "Old Laptop", 15000.0);

            when(mockValidator.validate(request)).thenReturn(Set.of());
            when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
            when(productRepository.existsByProductName("Laptop")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateProduct(1L, request)
            );

            assertTrue(exception.getMessage().contains("Category"));
            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("TC38: Cập nhật sản phẩm thất bại - ID không tồn tại")
        void testUpdateProduct_NotFound() {
            UpdateProductRequest request = new UpdateProductRequest("Updated Laptop", 20000.0, "Description", 15, "Electronics");
            
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.updateProduct(999L, request)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
            verify(mockValidator,  times(1)).validate(request);
        }
    }

    // ========================================================================================
    // D) TEST DELETE
    // ========================================================================================

    @Nested
    @DisplayName("Test deleteProduct() method")
    class DeleteProductTests {

        @Test
        @DisplayName("TC39: Xóa sản phẩm thành công")
        void testDeleteProduct_Success() {
            Product product = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000.0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).deleteById(1L);

            productService.deleteProduct(1L);

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("TC40: Xóa sản phẩm thất bại - ID không tồn tại")
        void testDeleteProduct_NotFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            NoSuchElementException exception = assertThrows(
                    NoSuchElementException.class,
                    () -> productService.deleteProduct(999L)
            );

            assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        }
    }

    // ========================================================================================
    // E) TEST GET ALL (PAGINATION)
    // ========================================================================================

        @Test
        @DisplayName("TC41: Lấy danh sách sản phẩm với pagination")
        void testGetAll_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = Arrays.asList(
                    new Product(1L, "Electronics", "Product 1", 10, "Laptop", 15000.0),
                    new Product(2L, "Books", "Product 2", 20, "Book", 50.0)
            );
            Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

            when(productRepository.findAll(pageable)).thenReturn(productPage);

            Page<ProductDto> result = productService.getAll(pageable);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(0, result.getNumber());
        }


}