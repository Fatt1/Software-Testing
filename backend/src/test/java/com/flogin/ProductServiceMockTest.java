package com.flogin;

import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.entity.Category;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import com.flogin.service.ProductService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductServiceMockTest - Mock Repository trong Service Tests (2.5 điểm)
 * Test service layer với mocked repository
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Mock ProductRepository trong Service Tests (2.5 điểm)")
public class ProductServiceMockTest {

    /**
     * a) Mock ProductRepository (1 điểm)
     */
    @Mock
    private ProductRepository productRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private CreateProductRequest mockCreateRequest;
    private UpdateProductRequest mockUpdateRequest;

    @BeforeEach
    void setUp() {
        mockProduct = new Product(1L, "Electronics", "Gaming laptop", 10, "Laptop", 15000000.0);
        mockCreateRequest = new CreateProductRequest("Laptop", 15000000.0, "Gaming laptop", 10, "Electronics");
        mockUpdateRequest = new UpdateProductRequest("Laptop", 15000000.0, "Gaming laptop", 10, "Electronics");
    }

    /**
     * A) Mock ProductRepository
     * Test các methods với mocked repository
     */
    @Nested
    @DisplayName("A) Mock ProductRepository (1 điểm)")
    class MockRepositoryTests {

        @Test
        @DisplayName("1. Mock findById - Tìm product thành công")
        void testGetProductById_Success() {
            // Arrange: Mock repository trả về product
            when(productRepository.findById(1L))
                .thenReturn(Optional.of(mockProduct));

            // Act
            ProductDto result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertEquals("Laptop", result.getProductName());
            assertEquals(15000000.0, result.getPrice());
            assertEquals("Electronics", result.getCategory());

            // c) Verify repository interaction
            verify(productRepository, times(1)).findById(1L);
        }



        @Test
        @DisplayName("2. Mock findById - Product không tồn tại")
        void testGetProductById_NotFound() {
            // Arrange: Mock repository trả về empty
            when(productRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                productService.getProductById(999L);
            });

            // c) Verify repository interaction
            verify(productRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("3. Mock save - Tạo product mới")
        void testCreateProduct_Success() {
            // Arrange
            when(validator.validate(any(CreateProductRequest.class)))
                    .thenReturn(Set.of());
            when(productRepository.save(any(Product.class)))
                    .thenReturn(mockProduct);

            // Act
            ProductDto result = productService.createProduct(mockCreateRequest);

            // Assert
            assertNotNull(result);
            assertEquals("Laptop", result.getProductName());

            // c) Verify interactions
            verify(validator, times(1)).validate(any(CreateProductRequest.class));
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    /**
     * B) Test Service Layer với Mocked Repository
     */
    @Nested
    @DisplayName("B) Test Service Layer với Mocked Repository (1 điểm)")
    class ServiceLayerTests {

        @Test
        @DisplayName("4. Service update product với mock repository")
        void testUpdateProduct_Success() {
            // Arrange
            when(validator.validate(any(UpdateProductRequest.class)))
                    .thenReturn(Set.of());
            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(mockProduct));
            when(productRepository.save(any(Product.class)))
                    .thenReturn(mockProduct);

            // Act
            ProductDto result = productService.updateProduct(1L, mockUpdateRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());

            // c) Verify repository được gọi đúng thứ tự
            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("5. Service delete product với mock repository")
        void testDeleteProduct_Success() {
            // Arrange
            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(mockProduct));
            doNothing().when(productRepository).deleteById(1L);

            // Act
            productService.deleteProduct(1L);

            // c) Verify repository interactions
            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("6. Service handle exception khi repository throw error")
        void testDeleteProduct_NotFound() {
            // Arrange
            when(productRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                productService.deleteProduct(999L);
            });

            // c) Verify deleteById KHÔNG được gọi khi product không tồn tại
            verify(productRepository, times(1)).findById(999L);
            verify(productRepository, never()).deleteById(anyLong());
        }
    }

    /**
     * C) Verify Repository Interactions
     */
    @Nested
    @DisplayName("C) Verify Repository Interactions (0.5 điểm)")
    class VerifyRepositoryInteractionsTests {

        @Test
        @DisplayName("7. Verify repository được gọi với argument cụ thể")
        void testVerify_RepositoryCalledWithSpecificArgument() {
            // Arrange
            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(mockProduct));

            // Act
            productService.getProductById(1L);

            // c) Verify với argument cụ thể
            verify(productRepository).findById(eq(1L));
            verify(productRepository, times(1)).findById(1L);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("8. Verify repository không được gọi khi validation fail")
        void testVerify_RepositoryNotCalledWhenValidationFails() {
            // Arrange: Mock validator trả về violation
            var violation = mock(jakarta.validation.ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Price phải > 0");
            when(validator.validate(any(CreateProductRequest.class)))
                    .thenReturn(Set.of(violation));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                productService.createProduct(mockCreateRequest);
            });

            // c) Verify repository.save() KHÔNG được gọi
            verify(productRepository, never()).save(any(Product.class));
            verifyNoInteractions(productRepository);
        }

        @Test
        @DisplayName("9. Verify multiple repository interactions")
        void testVerify_MultipleRepositoryInteractions() {
            // Arrange
            when(validator.validate(any(UpdateProductRequest.class)))
                    .thenReturn(Set.of());
            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(mockProduct));
            when(productRepository.save(any(Product.class)))
                    .thenReturn(mockProduct);

            // Act
            productService.updateProduct(1L, mockUpdateRequest);

            // c) Verify thứ tự gọi repository methods
            var inOrder = inOrder(productRepository);
            inOrder.verify(productRepository).findById(1L);
            inOrder.verify(productRepository).save(any(Product.class));

            // Verify tổng số lần gọi
            verify(productRepository, times(1)).findById(anyLong());
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("10. Verify với ArgumentCaptor")
        void testVerify_WithArgumentCaptor() {
            // Arrange
            when(validator.validate(any(CreateProductRequest.class)))
                    .thenReturn(Set.of());
            when(productRepository.save(any(Product.class)))
                    .thenReturn(mockProduct);

            // Act
            productService.createProduct(mockCreateRequest);

            // c) Verify với ArgumentCaptor
            var captor = org.mockito.ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(captor.capture());

            Product capturedProduct = captor.getValue();
            assertEquals("Laptop", capturedProduct.getProductName());
            assertEquals(15000000.0, capturedProduct.getPrice());
            assertEquals(Category.ELECTRONICS, capturedProduct.getCategory());
        }
    }
}
