package com.flogin;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;

import com.flogin.entity.User;
import com.flogin.repository.interfaces.UserRepository;
import com.flogin.service.AuthService;
import com.flogin.service.JwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * AuthService Unit Test với Bean Validation
 * Coverage >= 85%
 * Test scenarios:
 * - authenticate() với các trường hợp: success, user không tồn tại, password sai, validation errors
 * - Sử dụng Jakarta Bean Validation (@NotBlank, @Size, @Pattern) thay vì validate() thủ công
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Test - Bean Validation")
public class AuthServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Mock
    private JwtService mockJwtService;

    @Mock
    private Validator mockValidator;


    private Validator realValidator;

    private AuthService authService;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "testuser", "hashedPassword123", "testuser@example.com");
        authService = new AuthService(mockJwtService, mockUserRepository, mockPasswordEncoder, mockValidator);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        realValidator = factory.getValidator();
    }

    // A) TEST METHOD authenticate()

    @Test
    @DisplayName("TC1: Login thành công với credential hợp lệ")
    void testLoginSuccess() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testUser", "Test123");

        // Mock validator trả về không có lỗi
        when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of());

        // Mock các dependencies
        when(mockUserRepository.findByUserName(loginRequest.getUserName()))
                .thenReturn(Optional.of(mockUser));
        when(mockPasswordEncoder.matches("Test123", "hashedPassword123"))
                .thenReturn(true);
        when(mockJwtService.generateToken(mockUser))
                .thenReturn("fake-jwt-token");

        // Act
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        // Assert
        assertTrue(loginResponse.isSuccess(), "Login phải thành công");
        assertEquals("Login thành công", loginResponse.getMessage());
        assertNotNull(loginResponse.getToken(), "Token không được null");
        assertEquals("fake-jwt-token", loginResponse.getToken());

        // Verify UserDto
        assertNotNull(loginResponse.getUser(), "UserDto không được null");
        assertEquals("testuser", loginResponse.getUser().getUserName());
        assertEquals("testuser@example.com", loginResponse.getUser().getEmail());

        // Verify interactions
        verify(mockValidator, times(1)).validate(loginRequest);
        verify(mockUserRepository, times(1)).findByUserName("testUser");
        verify(mockPasswordEncoder, times(1)).matches("Test123", "hashedPassword123");
        verify(mockJwtService, times(1)).generateToken(mockUser);
    }

    @Test
    @DisplayName("TC2: Login thất bại khi username không tồn tại")
    void testLoginFailure_UserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("nonExistentUser", "Test123");

        // Mock validator trả về không có lỗi
        when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of());

        // Mock repository trả về empty
        when(mockUserRepository.findByUserName("nonExistentUser"))
                .thenReturn(Optional.empty());

        // Act
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        // Assert
        assertFalse(loginResponse.isSuccess(), "Login phải thất bại");
        assertEquals("Login thất bại với user name không tồn tại", loginResponse.getMessage());
        assertNull(loginResponse.getToken(), "Token phải là null");
        assertNull(loginResponse.getUser(), "UserDto phải là null khi thất bại");

        // Verify interactions
        verify(mockValidator, times(1)).validate(loginRequest);
        verify(mockUserRepository, times(1)).findByUserName("nonExistentUser");
        verify(mockPasswordEncoder, never()).matches(anyString(), anyString());
        verify(mockJwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("TC3: Login thất bại khi password sai")
    void testLoginFailure_WrongPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testUser", "WrongPassword123");

        // Mock validator trả về không có lỗi
        when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of());

        // Mock dependencies
        when(mockUserRepository.findByUserName("testUser"))
                .thenReturn(Optional.of(mockUser));
        when(mockPasswordEncoder.matches("WrongPassword123", "hashedPassword123"))
                .thenReturn(false);

        // Act
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        // Assert
        assertFalse(loginResponse.isSuccess(), "Login phải thất bại");
        assertEquals("Login với password sai", loginResponse.getMessage());
        assertNull(loginResponse.getToken(), "Token phải là null");
        assertNull(loginResponse.getUser(), "UserDto phải là null khi thất bại");

        // Verify interactions
        verify(mockValidator, times(1)).validate(loginRequest);
        verify(mockUserRepository, times(1)).findByUserName("testUser");
        verify(mockPasswordEncoder, times(1)).matches("WrongPassword123", "hashedPassword123");
        verify(mockJwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("TC4: Login thất bại với validation error - username null")
    void testLoginFailure_ValidationError_NullUsername() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(null, "Test123");

        // Mock validator trả về violation
        ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Username không được để trống");
        when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

        // Act
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        // Assert
        assertFalse(loginResponse.isSuccess(), "Login phải thất bại do validation");
        assertEquals("Username không được để trống", loginResponse.getMessage());
        assertNull(loginResponse.getToken());
        assertNull(loginResponse.getUser(), "UserDto phải là null khi validation fail");

        // Verify không có interaction với dependencies khác
        verify(mockValidator, times(1)).validate(loginRequest);
        verify(mockUserRepository, never()).findByUserName(anyString());
        verify(mockPasswordEncoder, never()).matches(anyString(), anyString());
        verify(mockJwtService, never()).generateToken(any(User.class));
    }


    @Test
    @DisplayName("TC5: Cả username và password đều null - Validator phải trả về nhiều lỗi")
    void validate_withBothNull_shouldReturnMultipleErrors() {
        // Arrange
        var request = new LoginRequest(null, null);

        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(request);
        // Act

        // Assert
        assertEquals(2, violations.size(), "Phải có đúng 2 lỗi");

        boolean isHasUserNameError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Username không được để trống"));
        assertTrue(isHasUserNameError);

        boolean isHasPasswordError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password không được để trống"));
        assertTrue(isHasPasswordError);
    }


    // ===========================Câu B==================================
    @Test
    @DisplayName("TC6: Login thất bại với validation error - password null")
    void testLoginFailure_ValidationError_NullPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testUser", null);

        // Mock validator trả về violation
        ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Password không được để trống");
        when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

        // Act
        LoginResponse loginResponse = authService.authenticate(loginRequest);

        // Assert
        assertFalse(loginResponse.isSuccess(), "Login phải thất bại do validation");
        assertEquals("Password không được để trống", loginResponse.getMessage());
        assertNull(loginResponse.getToken());

        // Verify không có interaction với dependencies
        verify(mockValidator, times(1)).validate(loginRequest);
        verify(mockUserRepository, never()).findByUserName(anyString());
    }

    @Test
    @DisplayName("TC7: Login thất bại với validation error - username quá ngắn")
    void testLoginFailure_ValidationError_ShortUsername() {
        // Arrange
        LoginRequest req = new LoginRequest("ab", "Test123");

        //Act
        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(req);

        //Assert
        assertFalse(violations.isEmpty(), "Phải có lỗi validate");
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("từ 3 đến 50 ký tự"));
        assertTrue(hasSizeError, "Phải chứa thông báo về độ dài username");
    }


    @Test
    @DisplayName("TC8: Login thất bại với validation error - password quá ngắn")
    void testLoginFailure_ValidationError_ShortPassword() {
        //Arrange
        LoginRequest req = new LoginRequest("testUser", "12345");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(req);

        //Assert
        assertFalse(violations.isEmpty(), "Phải có lỗi validate password");
        boolean isHasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password phải từ 6 đến 100 ký tự"));
        assertTrue(isHasError, "Phải có thông báo lỗi password");

    }

    @Test
    @DisplayName("TC9: Login thất bại với validation error - username có ký tự đặc biệt")
    void testLoginFailure_ValidationError_InvalidUsernameChars() {
        //Arrange
        LoginRequest req = new LoginRequest("user@#$#", "Test1234");

        //Act
        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(req);

        //Assert
        assertFalse(violations.isEmpty(), "Phải có lỗi username có chứa ký tự đặc biệt");
        boolean isHasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        assertTrue(isHasError, "Phải có thông báo lỗi username có ký tự đặc biệt");

    }

    @Test
    @DisplayName("TC10: Login thất bại với validation error - password thiếu chữ cái")
    void testLoginFailure_ValidationError_PasswordMissingLetter() {
        //Arrange
        LoginRequest req = new LoginRequest("testUser", "123456");

        //Act
        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(req);

        //Assert
        assertFalse(violations.isEmpty(), "Phải có lỗi password thiếu chữ cái");
        boolean isHasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password phải chứa ít nhất 1 chữ cái"));
        assertTrue(isHasError, "Phải có thông báo lỗi password thiếu chữ cái");
    }


    @Test
    @DisplayName("TC11: Login thất bại với validation error - password thiếu số")
    void testLoginFailure_ValidationError_PasswordMissingNumber() {
        // Arrange
        LoginRequest req = new LoginRequest("testUser", "testpassword");

        //Act
        Set<ConstraintViolation<LoginRequest>> violations = realValidator.validate(req);

        //Assert
        assertFalse(violations.isEmpty(), "Phải có lỗi password thiếu số");
        boolean isHasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password phải chứa ít nhất 1 chữ số"));
        assertTrue(isHasError, "Phải có thông báo lỗi password thiếu số");
    }
}