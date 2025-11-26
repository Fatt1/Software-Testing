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
 * ProductServiceMockTest - Repository Mocking in Service Layer Tests
 * 
 * Test Suite Purpose: (2.5 điểm total)
 * Demonstrates advanced repository mocking techniques in service layer testing.
 * Shows how to properly mock data access layer (Repository) when testing
 * business logic layer (Service).
 * 
 * Testing Strategy:
 * - Mock Repository: Replace real JPA repository with Mockito mock
 * - Inject Mocks: Automatically inject mocked dependencies into service
 * - Test Isolation: Test service business logic without database
 * - Behavior Verification: Verify service calls repository correctly
 * 
 * What is Repository Mocking?
 * In Spring Boot applications, the repository layer handles database operations.
 * Mocking the repository allows testing service business logic without:
 * - Setting up actual database
 * - Managing test data
 * - Dealing with database state
 * - Slow database queries
 * 
 * Mockito Annotations Used:
 * 
 * @Mock (1 điểm)
 * - Creates a Mockito mock object
 * - Replaces real dependency with controllable fake
 * - All methods return default values unless configured
 * - Enables interaction verification
 * 
 * Applied to:
 * - ProductRepository - Data access layer mock
 * - Validator - Bean validation mock
 * 
 * @InjectMocks
 * - Creates instance of class under test
 * - Automatically injects @Mock dependencies
 * - Uses constructor, setter, or field injection
 * - Simplifies test setup
 * 
 * Applied to:
 * - ProductService - Service under test with injected mocks
 * 
 * Dependency Injection Pattern:
 * @InjectMocks will inject mocks into ProductService:
 * 
 * class ProductService {
 *     private final ProductRepository repository; // @Mock injected here
 *     private final Validator validator;         // @Mock injected here
 *     
 *     // Constructor injection (preferred)
 *     public ProductService(ProductRepository repo, Validator val) {
 *         this.repository = repo;
 *         this.validator = val;
 *     }
 * }
 * 
 * Test Categories:
 * 
 * a) Mock ProductRepository (1 điểm)
 * - Verify repository is properly mocked
 * - Configure mock repository behavior
 * - Test service with mocked repository responses
 * 
 * b) Test Service with Mocked Repository (1 điểm)
 * - Create product - mock repository.save()
 * - Find product - mock repository.findById()
 * - Update product - mock find and save
 * - Delete product - mock repository.deleteById()
 * - Test service logic with various mock responses
 * 
 * c) Verify Repository Interactions (0.5 điểm)
 * - Verify repository methods were called
 * - Check correct arguments passed
 * - Validate call counts
 * - Ensure no unexpected interactions
 * 
 * Mock Configuration Patterns:
 * 
 * 1. Mock findById to return product:
 * when(productRepository.findById("product-id"))
 *     .thenReturn(Optional.of(mockProduct));
 * 
 * 2. Mock findById to return empty (not found):
 * when(productRepository.findById("invalid-id"))
 *     .thenReturn(Optional.empty());
 * 
 * 3. Mock save to return saved product:
 * when(productRepository.save(any(Product.class)))
 *     .thenReturn(savedProduct);
 * 
 * 4. Mock delete (void method):
 * doNothing().when(productRepository).deleteById("product-id");
 * 
 * Verification Patterns:
 * 
 * 1. Verify method was called:
 * verify(productRepository).save(any(Product.class));
 * 
 * 2. Verify with specific argument:
 * verify(productRepository).findById("product-123");
 * 
 * 3. Verify call count:
 * verify(productRepository, times(1)).save(any());
 * 
 * 4. Verify never called:
 * verify(productRepository, never()).deleteById(any());
 * 
 * Test Scenarios:
 * 
 * Create Product:
 * - Service receives CreateProductRequest
 * - Service validates request
 * - Service converts DTO to Entity
 * - Service calls repository.save()
 * - Repository returns saved entity
 * - Service converts entity to DTO
 * - Service returns ProductDto
 * 
 * Get Product:
 * - Service calls repository.findById()
 * - If found: return ProductDto
 * - If not found: throw NoSuchElementException
 * 
 * Update Product:
 * - Service calls repository.findById()
 * - If found: update fields, call repository.save()
 * - If not found: throw exception
 * 
 * Delete Product:
 * - Service calls repository.findById() to verify exists
 * - Service calls repository.deleteById()
 * - If not found: throw exception
 * 
 * Why Mock Repository in Service Tests?
 * - Isolation: Test business logic separately from database
 * - Speed: No database overhead, tests run fast
 * - Control: Predictable repository responses
 * - Simplicity: No test data setup required
 * - Focus: Test only service layer concerns
 * - Coverage: Easy to test error scenarios
 * 
 * @see com.flogin.service.ProductService - Service under test
 * @see com.flogin.repository.interfaces.ProductRepository - Mocked repository
 */

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
    
    /**
     * @InjectMocks tự động inject mock dependencies vào ProductService
     */
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
     * A) Mock ProductRepository (1 điểm)
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
     * B) Test Service Layer với Mocked Repository (1 điểm)
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
     * C) Verify Repository Interactions (0.5 điểm)
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
