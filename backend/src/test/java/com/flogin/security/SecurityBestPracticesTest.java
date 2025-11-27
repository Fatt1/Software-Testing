package com.flogin.security;

import com.flogin.entity.User;
import com.flogin.repository.interfaces.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security Best Practices Tests")
public class SecurityBestPracticesTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("1. Password Hashing - Mật khẩu được hash bằng BCrypt")
    void testPasswordHashing() {
        // Arrange
        String rawPassword = "mySecurePassword123";

        // Act - Hash password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(hashedPassword, "Hashed password không được null");
        assertNotEquals(rawPassword, hashedPassword, "Password không được lưu dạng plain text");
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"),
                "Password phải được hash bằng BCrypt");
        assertTrue(hashedPassword.length() >= 60, "BCrypt hash phải có độ dài >= 60 ký tự");

        // Verify password matching
        boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
        assertTrue(matches, "Password encoder phải verify được password đúng");

        // Verify wrong password doesn't match
        boolean wrongMatch = passwordEncoder.matches("wrongPassword", hashedPassword);
        assertFalse(wrongMatch, "Password encoder phải reject password sai");

        System.out.println("✓ Password Hashing Test PASSED");
        System.out.println("  Raw Password: " + rawPassword);
        System.out.println("  Hashed: " + hashedPassword);
    }


    // ========================================================================================
    // 3. CORS CONFIGURATION TEST
    // ========================================================================================

    @Test
    @DisplayName("3. CORS Configuration - CORS headers được cấu hình đúng")
    void testCorsConfiguration() throws Exception {
        // Arrange
        String origin = "http://localhost:5173";

        // Act - Preflight request (OPTIONS)
        mockMvc.perform(options("/api/products")
                        .header("Origin", origin)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type,Authorization"))

                // Assert
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Methods",
                        org.hamcrest.Matchers.containsString("POST")))
                .andExpect(header().exists("Access-Control-Allow-Headers"));

    }

    // ========================================================================================
    // 4. SECURITY HEADERS TEST
    // ========================================================================================

    @Test
    @DisplayName("4. Security Headers - Response có các security headers cần thiết")
    void testSecurityHeaders() throws Exception {
        // Arrange & Act
        MvcResult result = mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert - Kiểm tra các security headers quan trọng
        String xContentTypeOptions = result.getResponse().getHeader("X-Content-Type-Options");
        String xFrameOptions = result.getResponse().getHeader("X-Frame-Options");
        String xXssProtection = result.getResponse().getHeader("X-XSS-Protection");

        // Spring Security tự động thêm các headers này
        assertNotNull(xContentTypeOptions, "X-Content-Type-Options header phải có");
        assertEquals("nosniff", xContentTypeOptions,
                "X-Content-Type-Options phải là 'nosniff'");

        assertNotNull(xFrameOptions, "X-Frame-Options header phải có");
        assertTrue(xFrameOptions.equals("DENY") || xFrameOptions.equals("SAMEORIGIN"),
                "X-Frame-Options phải là DENY hoặc SAMEORIGIN");



    }
}
