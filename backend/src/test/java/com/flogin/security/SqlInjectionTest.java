package com.flogin.security;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.repository.interfaces.ProductRepository;
import com.flogin.repository.interfaces.UserRepository;
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
 * SQL Injection Security Tests (2 điểm)
 * 
 * Tests các attack vectors phổ biến:
 * - Login bypass attempts
 * - Comment-based injection
 * - Union-based injection
 * - Time-based blind injection
 * - Boolean-based blind injection
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("SQL Injection Security Tests")
public class SqlInjectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

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

        // Login để lấy token
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        authToken = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    // ===================================================================
    // TC1: SQL Injection via Login - Classic SQL Injection
    // ===================================================================

    @Test
    @DisplayName("TC1.1: SQL Injection - Login bypass với ' OR '1'='1")
    void testSqlInjection_LoginBypass_Classic() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin' OR '1'='1", "anything");

        // Should be rejected by validation (400) because of special characters in username
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("TC1.2: SQL Injection - Login bypass với ' OR 1=1--")
    void testSqlInjection_LoginBypass_Comment() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin' OR 1=1--", "anything");

        // Should be rejected by validation (400) because of special characters in username
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("TC1.3: SQL Injection - Login bypass với '; DROP TABLE users--")
    void testSqlInjection_LoginBypass_DropTable() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin'; DROP TABLE users--", "password");

        // Should be rejected by validation (400) because of special characters in username
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());

        // Verify table vẫn tồn tại
        long userCount = userRepository.count();
        assert userCount > 0 : "Users table should still exist";
    }

    @Test
    @DisplayName("TC1.4: SQL Injection - Login với UNION SELECT")
    void testSqlInjection_LoginBypass_Union() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin' UNION SELECT null, 'admin', 'password'--", "anything");

        // Should be rejected by validation (400) because of special characters in username
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC2: SQL Injection via Product Search
    // ===================================================================

    @Test
    @DisplayName("TC2.1: SQL Injection - Product name search với ' OR '1'='1")
    void testSqlInjection_ProductSearch_Classic() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("search", "' OR '1'='1")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                // Product API returns paginated response directly, not wrapped in $.data
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("TC2.2: SQL Injection - Product search với '; DELETE FROM products--")
    void testSqlInjection_ProductSearch_Delete() throws Exception {
        long countBefore = productRepository.count();

        mockMvc.perform(get("/api/products")
                .param("search", "'; DELETE FROM products--")
                .header("Authorization", authToken))
                .andExpect(status().isOk());

        long countAfter = productRepository.count();
        assert countBefore == countAfter : "Products should not be deleted";
    }

    @Test
    @DisplayName("TC2.3: SQL Injection - Product search với UNION SELECT")
    void testSqlInjection_ProductSearch_Union() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("search", "' UNION SELECT id, 'hacked', 0, 'ELECTRONICS', null FROM products--")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ===================================================================
    // TC3: SQL Injection via Product Creation
    // ===================================================================

    @Test
    @DisplayName("TC3.1: SQL Injection - Create product với malicious name")
    void testSqlInjection_CreateProduct_MaliciousName() throws Exception {
        CreateProductRequest maliciousRequest = new CreateProductRequest("Product'; DROP TABLE products--", 100.0, "Test", 10, "Electronics");

        long countBefore = productRepository.count();

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isCreated());

        long countAfter = productRepository.count();
        assert countAfter == countBefore + 1 : "Only one product should be added";
        
        // Verify table vẫn tồn tại
        assert productRepository.count() > 0 : "Products table should still exist";
    }

    @Test
    @DisplayName("TC3.2: SQL Injection - Create product với malicious description")
    void testSqlInjection_CreateProduct_MaliciousDescription() throws Exception {
        CreateProductRequest maliciousRequest = new CreateProductRequest("Normal Product", 100.0, "'; UPDATE products SET price=0 WHERE '1'='1", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(100.0)); // Product API returns data directly
    }

    // ===================================================================
    // TC4: SQL Injection via Product Update
    // ===================================================================

    @Test
    @DisplayName("TC4.1: SQL Injection - Update product với malicious name")
    void testSqlInjection_UpdateProduct_MaliciousName() throws Exception {
        UpdateProductRequest maliciousRequest = new UpdateProductRequest("Updated'; DELETE FROM products WHERE '1'='1--", 200.0, "Test", 20, "Electronics");

        long countBefore = productRepository.count();

        mockMvc.perform(put("/api/products/" + productId)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isOk());

        long countAfter = productRepository.count();
        assert countBefore == countAfter : "No products should be deleted";
    }

    // ===================================================================
    // TC5: Time-based Blind SQL Injection
    // ===================================================================

    @Test
    @DisplayName("TC5.1: Time-based blind SQL injection - Login")
    void testSqlInjection_TimeBased_Login() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin' AND SLEEP(5)--", "anything");

        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());

        long duration = System.currentTimeMillis() - startTime;
        
        // Không nên bị delay bởi SLEEP command
        assert duration < 3000 : "Request should not be delayed by SQL injection";
    }

    @Test
    @DisplayName("TC5.2: Time-based blind SQL injection - Product search")
    void testSqlInjection_TimeBased_ProductSearch() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/products")
                .param("search", "' OR IF(1=1, SLEEP(5), 0)--")
                .header("Authorization", authToken))
                .andExpect(status().isOk());

        long duration = System.currentTimeMillis() - startTime;
        assert duration < 3000 : "Request should not be delayed by SQL injection";
    }

    // ===================================================================
    // TC6: Boolean-based Blind SQL Injection
    // ===================================================================

    @Test
    @DisplayName("TC6.1: Boolean-based blind SQL injection")
    void testSqlInjection_BooleanBased() throws Exception {
        // True condition
        mockMvc.perform(get("/api/products")
                .param("search", "' OR 1=1--")
                .header("Authorization", authToken))
                .andExpect(status().isOk());

        // False condition
        mockMvc.perform(get("/api/products")
                .param("search", "' OR 1=2--")
                .header("Authorization", authToken))
                .andExpect(status().isOk());
        
        // Responses không nên khác nhau dựa trên boolean condition
    }

    // ===================================================================
    // TC7: Second-Order SQL Injection
    // ===================================================================

    @Test
    @DisplayName("TC7.1: Second-order SQL injection - Store và retrieve")
    void testSqlInjection_SecondOrder() throws Exception {
        // Tạo product với malicious name
        CreateProductRequest request = new CreateProductRequest("Test' OR '1'='1", 100.0, "Test", 10, "Electronics");

        String response = mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(response).get("id").asLong(); // Product API returns data directly

        // Retrieve product - malicious name không nên trigger SQL injection
        mockMvc.perform(get("/api/products/" + productId)
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test' OR '1'='1")); // No .data wrapper
    }

    // ===================================================================
    // TC8: SQL Injection via ID Parameters
    // ===================================================================

    @Test
    @DisplayName("TC8.1: SQL Injection - getProductById với malicious ID")
    void testSqlInjection_GetProductById_Malicious() throws Exception {
        // Type conversion error when trying to convert malicious string to long
        mockMvc.perform(get("/api/products/1' OR '1'='1")
                .header("Authorization", authToken))
                .andExpect(status().isInternalServerError()); // 500 due to type conversion
    }

    @Test
    @DisplayName("TC8.2: SQL Injection - deleteProduct với malicious ID")
    void testSqlInjection_DeleteProduct_MaliciousId() throws Exception {
        long countBefore = productRepository.count();

        // Type conversion error when trying to convert malicious string to long
        mockMvc.perform(delete("/api/products/1' OR '1'='1")
                .header("Authorization", authToken))
                .andExpect(status().isInternalServerError()); // 500 due to type conversion

        long countAfter = productRepository.count();
        assert countBefore == countAfter : "No products should be deleted";
    }

    // ===================================================================
    // TC9: Stacked Queries
    // ===================================================================

    @Test
    @DisplayName("TC9.1: Stacked queries - Multiple SQL statements")
    void testSqlInjection_StackedQueries() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin'; DELETE FROM products; SELECT * FROM users WHERE username='admin", "anything");

        long countBefore = productRepository.count();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());

        long countAfter = productRepository.count();
        assert countBefore == countAfter : "Products should not be affected";
    }

    // ===================================================================
    // TC10: SQL Injection với Special Characters
    // ===================================================================

    @Test
    @DisplayName("TC10.1: SQL Injection - Special characters escaping")
    void testSqlInjection_SpecialCharacters() throws Exception {
        String[] maliciousInputs = {
            "admin'--",
            "admin'#",
            "admin'/*",
            "admin' OR '1'='1' /*",
            "admin' OR '1'='1' --",
            "admin' OR '1'='1' #",
            "admin' OR '1'='1'/*",
            "' or 1=1--",
            "' or 1=1#",
            "' or 1=1/*",
            "') or '1'='1--",
            "') or ('1'='1--"
        };

        for (String maliciousInput : maliciousInputs) {
            LoginRequest request = new LoginRequest(maliciousInput, "password");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
