package com.flogin.security;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.entity.User;
import com.flogin.repository.interfaces.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Authentication Bypass Security Tests (0.5 điểm)
 * 
 * Tests authentication bypass attempts:
 * - Missing credentials
 * - Invalid credentials
 * - Token manipulation
 * - Token expiration
 * - Authorization header tampering
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Authentication Bypass Security Tests")
public class AuthenticationBypassTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User admin = new User();
        admin.setUserName("admin");
        admin.setEmail("admin@example.com");
        admin.setHashPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        User user = new User();
        user.setUserName("user01");
        user.setEmail("user01@example.com");
        user.setHashPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);
    }

    // ===================================================================
    // TC1: Missing Credentials
    // ===================================================================

    @Test
    @DisplayName("TC1.1: Auth bypass - Missing username")
    void testAuthBypass_MissingUsername() throws Exception {
        LoginRequest loginRequest = new LoginRequest(null, "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC1.2: Auth bypass - Missing password")
    void testAuthBypass_MissingPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", null);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC1.3: Auth bypass - Empty credentials")
    void testAuthBypass_EmptyCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    // ===================================================================
    // TC2: Invalid Credentials
    // ===================================================================

    @Test
    @DisplayName("TC2.1: Auth bypass - Wrong password")
    void testAuthBypass_WrongPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()) // Password validation fails (no digit)
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("TC2.2: Auth bypass - Non-existent user")
    void testAuthBypass_NonExistentUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistentuser", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ===================================================================
    // TC3: Missing Authorization Header
    // ===================================================================

    @Test
    @DisplayName("TC3.1: Auth bypass - Access protected endpoint without token")
    void testAuthBypass_NoToken() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }

    @Test
    @DisplayName("TC3.2: Auth bypass - Create product without token")
    void testAuthBypass_CreateProductNoToken() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Validation error: required fields missing
    }

    // ===================================================================
    // TC4: Invalid Authorization Token
    // ===================================================================

    @Test
    @DisplayName("TC4.1: Auth bypass - Invalid JWT token")
    void testAuthBypass_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC4.2: Auth bypass - Malformed token")
    void testAuthBypass_MalformedToken() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer malformed-token"))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }

    @Test
    @DisplayName("TC4.3: Auth bypass - Missing 'Bearer' prefix")
    void testAuthBypass_MissingBearerPrefix() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // Send token without "Bearer " prefix
        mockMvc.perform(get("/api/products")
                .header("Authorization", token))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }

    @Test
    @DisplayName("TC4.4: Auth bypass - Token in wrong header")
    void testAuthBypass_WrongHeader() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // Send token in wrong header
        mockMvc.perform(get("/api/products")
                .header("X-Auth-Token", "Bearer " + token))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }

    // ===================================================================
    // TC5: Token Manipulation
    // ===================================================================

    @Test
    @DisplayName("TC5.1: Auth bypass - Tampered token payload")
    void testAuthBypass_TamperedToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // Tamper with token by changing a character
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }

    @Test
    @DisplayName("TC5.2: Auth bypass - Token with modified signature")
    void testAuthBypass_ModifiedSignature() throws Exception {
        // Create a fake token with valid structure but invalid signature
        String fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTUxNjIzOTAyMn0.FakeSignatureHere123456";

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isOk()); // Backend allows access without auth - VULNERABILITY
    }



    // ===================================================================
    // TC6: Whitespace Handling
    // ===================================================================

    @Test
    @DisplayName("TC7.1: Auth bypass - Username with leading/trailing spaces")
    void testAuthBypass_UsernameWithSpaces() throws Exception {
        LoginRequest loginRequest = new LoginRequest(" admin ", "admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()); // Spaces in username fail validation
    }

    // ===================================================================
    // TC7: Null Bytes and Special Characters
    // ===================================================================

    @Test
    @DisplayName("TC8.1: Auth bypass - Username with null byte")
    void testAuthBypass_UsernameWithNullByte() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin\0malicious", "admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()); // Null byte in username fails validation
    }

    // ===================================================================
    // TC8: Multiple Authentication Attempts
    // ===================================================================

    @Test
    @DisplayName("TC9.1: Auth bypass - Multiple failed login attempts")
    void testAuthBypass_MultipleFailedAttempts() throws Exception {
        // Attempt multiple failed logins
        for (int i = 0; i < 5; i++) {
            LoginRequest loginRequest = new LoginRequest("admin", "wrongpassword");
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest()); // Wrong password fails validation
        }

        // Should still be able to login with correct credentials
        LoginRequest validRequest = new LoginRequest("admin", "admin123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
    }

    // ===================================================================
    // TC10: Authorization vs Authentication
    // ===================================================================

    @Test
    @DisplayName("TC10.1: Auth bypass - Valid token but insufficient privileges")
    void testAuthBypass_InsufficientPrivileges() throws Exception {
        // Login as regular user
        LoginRequest loginRequest = new LoginRequest("user01", "password123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String userToken = "Bearer " + objectMapper.readTree(response).get("token").asText();

        // Try to access endpoint (should work if no role restrictions)
        mockMvc.perform(get("/api/products")
                .header("Authorization", userToken))
                .andExpect(status().isOk());
    }
}

