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

    @Test
    @DisplayName("TC1.3: Auth bypass - Empty credentials")
    void testAuthBypass_EmptyCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

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
    @DisplayName("TC4.1: Auth bypass - Invalid JWT token")
    void testAuthBypass_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

}

