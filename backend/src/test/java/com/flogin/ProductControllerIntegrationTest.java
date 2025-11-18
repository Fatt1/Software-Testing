package com.flogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flogin.controller.ProductController;
import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductControllerIntegrationTest - Integration Test cho Product API
 * Test các CRUD operations: Create, Read, Update, Delete
 */
@WebMvcTest(ProductController.class)
@DisplayName("Product API Integration Tests")
public class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private ProductService productService;

    /**
     * A) Test POST /api/products - Create (1 điểm)
     * Test tạo product mới
     */
    @Nested
    @DisplayName("A) Test POST /api/products - Create (1 điểm)")
    class CreateProductTests {
        
        @Test
        @DisplayName("1. Tạo product thành công với dữ liệu hợp lệ")
        void testCreateProduct_Success() throws Exception {
            // Arrange
            CreateProductRequest requestDto = new CreateProductRequest("Laptop", 15000000.0, "Gaming laptop", 10, "Electronics");
            ProductDto responseDto = new ProductDto(1L, "Electronics", 15000000.0, "Laptop", "Gaming laptop", 10);
            
            when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(responseDto);
            
            // Act & Assert
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated()) // 201 Created
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productName").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(15000000.0))
                    .andExpect(jsonPath("$.quantity").value(10))
                    .andExpect(jsonPath("$.category").value("Electronics"));
            
            verify(productService, times(1)).createProduct(any(CreateProductRequest.class));
        }

        @Test
        @DisplayName("2. Tạo product thất bại - ProductName trống")
        void testCreateProduct_EmptyProductName() throws Exception {
            // Arrange: ProductName rỗng vi phạm @NotBlank
            CreateProductRequest requestDto = new CreateProductRequest("", 15000000.0, "Description", 10, "Electronics");
            
            // Act & Assert
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest()); // 400 Bad Request
            
            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("3. Tạo product thất bại - Price <= 0")
        void testCreateProduct_InvalidPrice() throws Exception {
            // Arrange: Price = 0 vi phạm @DecimalMin
            CreateProductRequest requestDto = new CreateProductRequest("Laptop", 0.0, "Description", 10, "Electronics");
            
            // Act & Assert
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
            
            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("4. Tạo product thất bại - Quantity < 0")
        void testCreateProduct_NegativeQuantity() throws Exception {
            // Arrange: Quantity âm vi phạm @Min(0)
            CreateProductRequest requestDto = new CreateProductRequest("Laptop", 15000000.0, "Description", -1, "Electronics");
            
            // Act & Assert
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
            
            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("5. Tạo product thất bại - Category không hợp lệ")
        void testCreateProduct_InvalidCategory() throws Exception {
            // Arrange
            CreateProductRequest requestDto = new CreateProductRequest("Laptop", 15000000.0, "Description", 10, "InvalidCategory");
            
            when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("Category 'InvalidCategory' không hợp lệ"));
            
            // Act & Assert
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    /**
     * B) Test GET /api/products - Read All (1 điểm)
     * Test lấy danh sách tất cả products
     */
        @Test
        @DisplayName("(B) Lấy danh sách products thành công")
        void testGetAllProducts_Success() throws Exception {
            // Arrange
            List<ProductDto> products = Arrays.asList(
                new ProductDto(1L, "Electronics", 15000000.0, "Laptop", "Gaming laptop", 10),
                new ProductDto(2L, "Electronics", 200000.0, "Mouse", "Wireless mouse", 50)
            );
            Page<ProductDto> page = new PageImpl<>(products, PageRequest.of(0, 10), products.size());
            
            when(productService.getAll(any()))
                .thenReturn(page);
            
            // Act & Assert
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].productName").value("Laptop"))
                    .andExpect(jsonPath("$.content[1].productName").value("Mouse"))
                    .andExpect(jsonPath("$.totalElements").value(2));
            
            verify(productService, times(1)).getAll(any());
        }

    /**
     * C) Test GET /api/products/{id} - Read One (1 điểm)
     * Test lấy thông tin một product theo ID
     */
    @Nested
    @DisplayName("C) Test GET /api/products/{id} - Read One (1 điểm)")
    class GetProductByIdTests {
        
        @Test
        @DisplayName("1. Lấy product theo ID thành công")
        void testGetProductById_Success() throws Exception {
            // Arrange
            ProductDto product = new ProductDto(1L, "Electronics", 15000000.0, "Laptop", "Gaming laptop", 10);
            
            when(productService.getProductById(1L))
                .thenReturn(product);
            
            // Act & Assert
            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productName").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(15000000.0))
                    .andExpect(jsonPath("$.category").value("Electronics"));
            
            verify(productService, times(1)).getProductById(1L);
        }

        @Test
        @DisplayName("2. Lấy product không tồn tại - 404 Not Found")
        void testGetProductById_NotFound() throws Exception {
            // Arrange
            when(productService.getProductById(999L))
                .thenThrow(new NoSuchElementException("Product not found with id: 999"));
            
            // Act & Assert
            mockMvc.perform(get("/api/products/999"))
                    .andExpect(status().isNotFound()); // 404 Not Found
            
            verify(productService, times(1)).getProductById(999L);
        }


    }

    /**
     * D) Test PUT /api/products/{id} - Update (1 điểm)
     * Test cập nhật product
     */
    @Nested
    @DisplayName("D) Test PUT /api/products/{id} - Update (1 điểm)")
    class UpdateProductTests {
        
        @Test
        @DisplayName("1. Cập nhật product thành công")
        void testUpdateProduct_Success() throws Exception {
            // Arrange
            UpdateProductRequest requestDto = new UpdateProductRequest("Laptop Updated", 16000000.0, "New description", 15, "Electronics");
            ProductDto responseDto = new ProductDto(1L, "Electronics", 16000000.0, "Laptop Updated", "New description", 15);
            
            when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                .thenReturn(responseDto);
            
            // Act & Assert
            mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productName").value("Laptop Updated"))
                    .andExpect(jsonPath("$.price").value(16000000.0))
                    .andExpect(jsonPath("$.quantity").value(15));
            
            verify(productService, times(1)).updateProduct(eq(1L), any(UpdateProductRequest.class));
        }

        @Test
        @DisplayName("2. Cập nhật product không tồn tại - 404 Not Found")
        void testUpdateProduct_NotFound() throws Exception {
            // Arrange
            UpdateProductRequest requestDto = new UpdateProductRequest("Laptop", 15000000.0, "Description", 10, "Electronics");
            
            when(productService.updateProduct(eq(999L), any(UpdateProductRequest.class)))
                .thenThrow(new NoSuchElementException("Product not found with id: 999"));
            
            // Act & Assert
            mockMvc.perform(put("/api/products/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("3. Cập nhật product với dữ liệu không hợp lệ - 400 Bad Request")
        void testUpdateProduct_InvalidData() throws Exception {
            // Arrange: ProductName trống
            UpdateProductRequest requestDto = new UpdateProductRequest("", 15000000.0, "Description", 10, "Electronics");
            
            // Act & Assert
            mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
            
            verify(productService, never()).updateProduct(eq(1L), any());
        }

        @Test
        @DisplayName("4. Cập nhật product với category không hợp lệ")
        void testUpdateProduct_InvalidCategory() throws Exception {
            // Arrange
            UpdateProductRequest requestDto = new UpdateProductRequest("Laptop", 15000000.0, "Description", 10, "InvalidCategory");
            
            when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("Category 'InvalidCategory' không hợp lệ"));
            
            // Act & Assert
            mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    /**
     * E) Test DELETE /api/products/{id} - Delete (1 điểm)
     * Test xóa product
     */
    @Nested
    @DisplayName("E) Test DELETE /api/products/{id} - Delete (1 điểm)")
    class DeleteProductTests {
        
        @Test
        @DisplayName("1. Xóa product thành công - 204 No Content")
        void testDeleteProduct_Success() throws Exception {
            // Arrange
            doNothing().when(productService).deleteProduct(1L);
            
            // Act & Assert
            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent()); // 204 No Content
            
            verify(productService, times(1)).deleteProduct(1L);
        }

        @Test
        @DisplayName("2. Xóa product không tồn tại - 404 Not Found")
        void testDeleteProduct_NotFound() throws Exception {
            // Arrange
            doThrow(new NoSuchElementException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);
            
            // Act & Assert
            mockMvc.perform(delete("/api/products/999"))
                    .andExpect(status().isNotFound());
            
            verify(productService, times(1)).deleteProduct(999L);
        }


    }

}
