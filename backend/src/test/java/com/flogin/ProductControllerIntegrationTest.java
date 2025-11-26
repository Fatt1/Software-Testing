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
        @DisplayName("1. Xóa product thành công - 204 No Content")
        void testDeleteProduct_Success() throws Exception {
            // Arrange
            doNothing().when(productService).deleteProduct(1L);
            
            // Act & Assert
            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent()); // 204 No Content
            
            verify(productService, times(1)).deleteProduct(1L);
        }
}
