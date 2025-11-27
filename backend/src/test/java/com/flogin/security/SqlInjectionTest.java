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
        userRepository.deleteAll();
        com.flogin.entity.User admin = new com.flogin.entity.User();
        admin.setUserName("admin");
        admin.setEmail("admin@example.com");
        admin.setHashPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

    }

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


        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("TC1.3: SQL Injection - Login bypass với '; DROP TABLE users--")
    void testSqlInjection_LoginBypass_DropTable() throws Exception {
        LoginRequest maliciousRequest = new LoginRequest("admin'; DROP TABLE users--", "password22");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());

        // Verify table vẫn tồn tại
        long userCount = userRepository.count();
        assert userCount > 0 : "Users table should still exist";
    }


}
