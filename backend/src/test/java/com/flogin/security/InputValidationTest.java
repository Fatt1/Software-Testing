package com.flogin.security;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Input Validation và Sanitization Tests (3 điểm)
 * 
 * Tests input validation:
 * - Length constraints
 * - Type validation
 * - Range validation
 * - Format validation
 * - Special characters handling
 * - Null/Empty validation
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Input Validation and Sanitization Tests")
public class InputValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.flogin.repository.interfaces.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        com.flogin.entity.User admin = new com.flogin.entity.User();
        admin.setUserName("admin");
        admin.setEmail("admin@example.com");
        admin.setHashPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        authToken = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    // ===================================================================
    // TC1: Product Name Validation
    // ===================================================================

    @Test
    @DisplayName("TC1.1: Validation - Product name quá ngắn (< 3 chars)")
    void testValidation_ProductName_TooShort() throws Exception {
        CreateProductRequest request = new CreateProductRequest("AB", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productName").exists());
    }

    @Test
    @DisplayName("TC1.2: Validation - Product name quá dài (> 100 chars)")
    void testValidation_ProductName_TooLong() throws Exception {
        CreateProductRequest request = new CreateProductRequest("A".repeat(101), 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productName").exists());
    }

    @Test
    @DisplayName("TC1.3: Validation - Product name null")
    void testValidation_ProductName_Null() throws Exception {
        CreateProductRequest request = new CreateProductRequest(null, 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productName").exists());
    }

    @Test
    @DisplayName("TC1.4: Validation - Product name empty string")
    void testValidation_ProductName_Empty() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC1.5: Validation - Product name chỉ có whitespace")
    void testValidation_ProductName_OnlyWhitespace() throws Exception {
        CreateProductRequest request = new CreateProductRequest("   ", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC2: Price Validation
    // ===================================================================

    @Test
    @DisplayName("TC2.1: Validation - Price âm")
    void testValidation_Price_Negative() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", -10.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("TC2.2: Validation - Price bằng 0")
    void testValidation_Price_Zero() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 0.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("TC2.3: Validation - Price quá lớn")
    void testValidation_Price_TooLarge() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 1_000_000_000.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("TC2.4: Validation - Price null")
    void testValidation_Price_Null() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", null, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC2.5: Validation - Price với decimal hợp lệ")
    void testValidation_Price_ValidDecimal() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 99.99, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(99.99));
    }

    // ===================================================================
    // TC3: Quantity Validation
    // ===================================================================

    @Test
    @DisplayName("TC3.1: Validation - Quantity âm")
    void testValidation_Quantity_Negative() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "Test", -5, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    @DisplayName("TC3.2: Validation - Quantity quá lớn")
    void testValidation_Quantity_TooLarge() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "Test", 100_000, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    @DisplayName("TC3.3: Validation - Quantity null")
    void testValidation_Quantity_Null() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "Test", null, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC4: Description Validation
    // ===================================================================

    @Test
    @DisplayName("TC4.1: Validation - Description quá dài (> 500 chars)")
    void testValidation_Description_TooLong() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "A".repeat(501), 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    @DisplayName("TC4.2: Validation - Description null (allowed)")
    void testValidation_Description_Null() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, null, 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ===================================================================
    // TC5: Category Validation
    // ===================================================================

    @Test
    @DisplayName("TC5.1: Validation - Category null")
    void testValidation_Category_Null() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "Test", 10, null);

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC5.2: Validation - Invalid category string")
    void testValidation_Category_Invalid() throws Exception {
        String jsonRequest = """
            {
                "productName": "Test Product",
                "price": 100.0,
                "description": "Test",
                "quantity": 10,
                "category": "INVALID_CATEGORY"
            }
            """;

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC6: Special Characters Handling
    // ===================================================================

    @Test
    @DisplayName("TC6.1: Sanitization - Special characters trong product name")
    void testSanitization_SpecialCharacters_ProductName() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Product <>&\"'", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Product <>&\"'"));
    }

    @Test
    @DisplayName("TC6.2: Sanitization - Unicode characters")
    void testSanitization_UnicodeCharacters() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Sản phẩm điện tử 📱", 100.0, "Mô tả sản phẩm", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Sản phẩm điện tử 📱"));
    }

    // ===================================================================
    // TC7: Login Input Validation
    // ===================================================================

    @Test
    @DisplayName("TC7.1: Validation - Username quá ngắn")
    void testValidation_Login_UsernameTooShort() throws Exception {
        LoginRequest request = new LoginRequest("ab", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC7.2: Validation - Password quá ngắn")
    void testValidation_Login_PasswordTooShort() throws Exception {
        LoginRequest request = new LoginRequest("admin", "123"); // < 6 chars

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC8: Type Validation
    // ===================================================================

    @Test
    @DisplayName("TC8.1: Validation - Price với string thay vì number")
    void testValidation_Type_PriceAsString() throws Exception {
        String jsonRequest = """
            {
                "productName": "Test Product",
                "price": "not a number",
                "description": "Test",
                "quantity": 10,
                "category": "ELECTRONICS"
            }
            """;

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC8.2: Validation - Quantity với decimal thay vì integer")
    void testValidation_Type_QuantityAsDecimal() throws Exception {
        String jsonRequest = """
            {
                "productName": "Test Product",
                "price": 100.0,
                "description": "Test",
                "quantity": 10.5,
                "category": "ELECTRONICS"
            }
            """;

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC9: Multiple Validation Errors
    // ===================================================================

    @Test
    @DisplayName("TC9.1: Validation - Multiple invalid fields")
    void testValidation_MultipleErrors() throws Exception {
        CreateProductRequest request = new CreateProductRequest("AB", -10.0, "A".repeat(501), -5, null);

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(4))));
    }

    // ===================================================================
    // TC10: Boundary Value Testing
    // ===================================================================

    @Test
    @DisplayName("TC10.1: Boundary - Product name exactly 3 chars")
    void testBoundary_ProductName_Min() throws Exception {
        CreateProductRequest request = new CreateProductRequest("ABC", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TC10.2: Boundary - Product name exactly 100 chars")
    void testBoundary_ProductName_Max() throws Exception {
        CreateProductRequest request = new CreateProductRequest("A".repeat(100), 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TC10.3: Boundary - Price = 0.01 (min valid)")
    void testBoundary_Price_Min() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 0.01, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(0.01));
    }

    @Test
    @DisplayName("TC10.4: Boundary - Quantity = 0 (min valid)")
    void testBoundary_Quantity_Min() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.0, "Test", 0, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(0));
    }
}
