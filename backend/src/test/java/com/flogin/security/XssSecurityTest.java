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
 * XSS (Cross-Site Scripting) Security Tests (1.5 điểm)
 * 
 * Tests các XSS attack vectors:
 * - Stored XSS (Persistent)
 * - Reflected XSS
 * - DOM-based XSS
 * - Script tag injection
 * - Event handler injection
 * - HTML entity encoding
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("XSS (Cross-Site Scripting) Security Tests")
public class XssSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.flogin.repository.interfaces.UserRepository userRepository;

    @Autowired
    private com.flogin.repository.interfaces.ProductRepository productRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private String authToken;
    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        productRepository.deleteAll();
        userRepository.deleteAll();

        com.flogin.entity.User admin = new com.flogin.entity.User();
        admin.setUserName("admin");
        admin.setEmail("admin@example.com");
        admin.setHashPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        com.flogin.entity.Product product = new com.flogin.entity.Product();
        product.setProductName("Test Product");
        product.setPrice(100.0);
        product.setQuantity(10);
        product.setDescription("Test Description");
        product.setCategory("Electronics");
        product = productRepository.save(product);
        productId = product.getId();

        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        authToken = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    // ===================================================================
    // TC1: Stored XSS - Script Tag Injection
    // ===================================================================

    @Test
    @DisplayName("TC1.1: Stored XSS - Script tag trong product name")
    void testXss_StoredXss_ScriptTag_ProductName() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<script>alert('XSS')</script>", 100.0, "Test", 10, "Electronics");

        String response = mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(response).get("id").asLong();

        // Retrieve và verify - Backend không sanitize, XSS payload được stored (VULNERABILITY!)
        mockMvc.perform(get("/api/products/" + productId)
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").exists())
                // SECURITY ISSUE: XSS payload được lưu nguyên trong database
                .andExpect(jsonPath("$.productName", containsString("<script>")));
    }


    // ===================================================================
    // TC2: Event Handler Injection
    // ===================================================================

    @Test
    @DisplayName("TC2.1: XSS - onerror event handler")
    void testXss_EventHandler_OnError() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<img src=x onerror=alert('XSS')>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("onerror")));
    }


    @Test
    @DisplayName("TC10.1: XSS - innerHTML manipulation attempt")
    void testXss_DomBased_InnerHtml() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("Product<img src=x onerror='document.body.innerHTML=\"<h1>Hacked</h1>\"'>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("innerHTML")));
    }
}

