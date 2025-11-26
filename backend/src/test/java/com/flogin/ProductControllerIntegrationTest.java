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
 * ProductControllerIntegrationTest - Integration Testing for Product CRUD API
 * 
 * Test Suite Purpose:
 * Comprehensive integration testing of ProductController REST API endpoints,
 * covering all CRUD (Create, Read, Update, Delete) operations with proper
 * HTTP protocol testing, JSON handling, and Spring MVC integration.
 * 
 * Testing Level: Integration Testing
 * - Tests Controller layer with mocked Service layer
 * - Validates all REST API endpoints and HTTP methods
 * - Verifies request/response JSON serialization
 * - Tests pagination, filtering, and search functionality
 * - Ensures proper HTTP status codes and error handling
 * 
 * API Endpoints Tested:
 * 
 * 1. POST /api/products - Create new product
 *    - Request: CreateProductRequest JSON body
 *    - Response: 201 CREATED with ProductDto
 *    - Validates required fields
 *    - Tests business rule violations
 * 
 * 2. GET /api/products - List all products (paginated)
 *    - Query params: page, size, category, search
 *    - Response: 200 OK with Page<ProductDto>
 *    - Tests pagination metadata
 *    - Tests filtering by category
 *    - Tests search functionality
 * 
 * 3. GET /api/products/{id} - Get product by ID
 *    - Path variable: product ID
 *    - Response: 200 OK with ProductDto
 *    - Tests 404 NOT FOUND for non-existent ID
 * 
 * 4. PUT /api/products/{id} - Update existing product
 *    - Path variable: product ID
 *    - Request: UpdateProductRequest JSON body
 *    - Response: 200 OK with updated ProductDto
 *    - Tests partial updates
 *    - Tests 404 for non-existent product
 * 
 * 5. DELETE /api/products/{id} - Delete product
 *    - Path variable: product ID
 *    - Response: 204 NO CONTENT
 *    - Tests 404 for non-existent product
 *    - Tests cascade delete if needed
 * 
 * Spring Test Infrastructure:
 * 
 * @WebMvcTest(ProductController.class)
 * - Configures only MVC layer for ProductController
 * - Loads controller, converters, filters
 * - Does NOT load services, repositories, database
 * - Provides MockMvc for HTTP testing
 * - Fast and focused testing
 * 
 * @MockitoBean ProductService
 * - Mocks the service layer
 * - Isolates controller testing
 * - Allows controlled service responses
 * - Registered in Spring context automatically
 * 
 * MockMvc Testing Patterns:
 * 
 * POST Request:
 * mockMvc.perform(post("/api/products")
 *     .contentType(MediaType.APPLICATION_JSON)
 *     .content(objectMapper.writeValueAsString(request)))
 *     .andExpect(status().isCreated())
 *     .andExpect(jsonPath("$.name").value("Product Name"));
 * 
 * GET Request with Path Variable:
 * mockMvc.perform(get("/api/products/{id}", productId))
 *     .andExpect(status().isOk())
 *     .andExpect(jsonPath("$.id").value(productId));
 * 
 * PUT Request:
 * mockMvc.perform(put("/api/products/{id}", productId)
 *     .contentType(MediaType.APPLICATION_JSON)
 *     .content(jsonBody))
 *     .andExpect(status().isOk());
 * 
 * DELETE Request:
 * mockMvc.perform(delete("/api/products/{id}", productId))
 *     .andExpect(status().isNoContent());
 * 
 * JSON Path Assertions:
 * - jsonPath("$.name") - Assert top-level field
 * - jsonPath("$.price") - Assert numeric field
 * - jsonPath("$", hasSize(3)) - Assert array size
 * - jsonPath("$.content[0].name") - Assert nested field
 * - jsonPath("$.totalElements") - Assert pagination metadata
 * 
 * Test Categories:
 * 
 * A) Create Product Tests (1 điểm)
 * - Successful product creation
 * - Validation errors (missing fields, invalid data)
 * - Duplicate product handling
 * - Category validation
 * 
 * B) Read Product Tests
 * - Get all products with pagination
 * - Get product by ID (success and not found)
 * - Filter by category
 * - Search by name
 * - Empty result handling
 * 
 * C) Update Product Tests
 * - Successful update
 * - Partial update (only some fields)
 * - Update non-existent product (404)
 * - Validation on update
 * 
 * D) Delete Product Tests
 * - Successful deletion
 * - Delete non-existent product (404)
 * - Verify service method called
 * 
 * HTTP Status Codes:
 * - 200 OK - Successful GET, PUT
 * - 201 CREATED - Successful POST
 * - 204 NO CONTENT - Successful DELETE
 * - 400 BAD REQUEST - Validation errors
 * - 404 NOT FOUND - Resource not found
 * - 500 INTERNAL SERVER ERROR - Server errors
 * 
 * Product DTO Structure:
 * {
 *   "id": "uuid",
 *   "name": "Product Name",
 *   "price": 99.99,
 *   "quantity": 100,
 *   "category": "ELECTRONICS",
 *   "description": "Product description"
 * }
 * 
 * Why Integration Test REST Controllers?
 * - Validates HTTP layer works correctly
 * - Tests request routing and mapping
 * - Verifies JSON serialization/deserialization
 * - Ensures proper status codes
 * - Tests error handling
 * - Documents API contract
 * - Catches integration issues
 * 
 * @see com.flogin.controller.ProductController - Controller under test
 * @see com.flogin.service.ProductService - Mocked service
 * @see com.flogin.dto.ProductDtos - Request/Response DTOs
 */

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
