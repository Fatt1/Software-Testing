package com.flogin;

import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import com.flogin.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Product service Unit Test")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("TC1: Tạo sản pham thành công")
    void testCreateProduct() {
        // Arrange
        ProductDto productDto = new ProductDto("Laptop", 15000, "" ,10, "Electronics");
        Product product = new Product(1L, "Electronics", "", 10, "Laptop", 150000);


        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
       ProductDto result =  productService.createProduct(productDto);

        // Assert
       assertNotNull(result);
       assertEquals("Laptop", productDto.getProductName());
       verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    @DisplayName("[TC06] Create Product: Invalid Name (too short)")
    void createProduct_shouldThrowException_whenNameIsInvalid() {
        // Arrange
        ProductDto productDto = new ProductDto("a", 15000, "" ,10, "Electronics");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(productDto)
        );

        assertTrue(exception.getMessage().contains("Product Name phải từ 3 đến 100 ký tự"));
        verify(productRepository, never()).save(any()); // Không được gọi save
    }

    @Test
    @DisplayName("[TC07] Create Product: Invalid Price (zero)")
    void createProduct_shouldThrowException_whenPriceIsInvalid() {
        // Arrange
        ProductDto productDto = new ProductDto("a", 0, "" ,10, "Electronics");


        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(productDto)
        );

        assertTrue(exception.getMessage().contains("Price phải > 0"));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("[TC08] Create Product: Invalid Category")
    void createProduct_shouldThrowException_whenCategoryIsInvalid() {
        // Arrange
        ProductDto productDto = new ProductDto("a", 0, "" ,10, "Invalid Category");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(productDto)
        );

        assertTrue(exception.getMessage().contains("Category"));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("[TC02] Get Product by ID: Happy Path (Found)")
    void getProductById_shouldReturnProduct_whenIdExists() {
        // Arrange

        Product product = new Product(1L, "Electronics", "", 10, "Laptop", 150000);


        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductDto result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getProductName());
        verify(productRepository, times(1)).findById(1L);
    }



}
