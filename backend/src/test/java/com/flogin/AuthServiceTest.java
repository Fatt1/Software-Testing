package com.flogin;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.entity.User;
import com.flogin.repository.interfaces.JwtService;
import com.flogin.repository.interfaces.PasswordEncoder;
import com.flogin.repository.interfaces.UserRepository;
import com.flogin.service.AuthService;
import com.flogin.service.AuthValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login Service Unit Test")
public class AuthServiceTest {
    // 1. Tạo các đối tượng giả (Mocks)
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private JwtService mockJwtService;


    //2. Tiêm các mock trên vào AuthService
    @InjectMocks
    private AuthService authService;

    // Biến user dùng chung
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "testuser", "hashedPassword123");
    }

    @Test
    @DisplayName("TC1: Login thành công với credential hợp le")
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("testUser", "Test123");

        // "Dạy" cho các mock biết phải làm gì

        // 3.2. Khi tìm user "testuser" -> trả về user giả đã tạo
        when(mockUserRepository.findByUserName(loginRequest.getUserName())).thenReturn(Optional.of(mockUser));

        when(mockPasswordEncoder.matches("Test123", "hashedPassword123")).thenReturn(true);

        // 3.4. Khi tạo token -> trả về một token giả
        when(mockJwtService.generateToken(mockUser)).thenReturn("fake-jwt-token");

        //4. Chạy method
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        //5. Kiem tra ket qua
        assertTrue(loginResponse.isSuccess());
        assertEquals("Login thành công", loginResponse.getMessage());
        assertNotNull(loginResponse.getToken());
    }

    @Test
    @DisplayName("TC2: Login that bại khi user name không tồn tại")
    void testLoginFailure_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("testWrongUser", "Test123");

        // "Dạy" cho các mock biết phải làm gì


        // 3.2. Khi tìm user "testuser" -> trả về user giả đã tạo
        when(mockUserRepository.findByUserName("testWrongUser")).thenReturn(Optional.empty());

        //4. Chạy method
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        //5. Kiem tra ket qua
        assertFalse(loginResponse.isSuccess());
        assertEquals("Login thất bại với user name không tồn tại", loginResponse.getMessage());
        assertNull(loginResponse.getToken());

    }

}
