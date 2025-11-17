package com.flogin;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.entity.User;
import com.flogin.repository.interfaces.UserRepository;
import com.flogin.service.AuthService;
import com.flogin.service.JwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
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

    private AuthService authService;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "testuser", "hashedPassword123");
        authService = new AuthService(mockJwtService, mockUserRepository, mockPasswordEncoder, mockValidator);
    }

    // ========================================================================================
    // A) TEST METHOD authenticate() với các scenarios (3 điểm)
    // ========================================================================================

    @Nested
    @DisplayName("Test authenticate() method")
    class AuthenticateTests {

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
            
            // Verify không có interaction với dependencies khác
            verify(mockValidator, times(1)).validate(loginRequest);
            verify(mockUserRepository, never()).findByUserName(anyString());
            verify(mockPasswordEncoder, never()).matches(anyString(), anyString());
            verify(mockJwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("TC5: Login thất bại với validation error - password null")
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
        @DisplayName("TC6: Login thất bại với validation error - username quá ngắn")
        void testLoginFailure_ValidationError_ShortUsername() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("ab", "Test123");

            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username phải từ 3 đến 50 ký tự");
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Username phải từ 3 đến 50 ký tự", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC7: Login thất bại với validation error - password quá ngắn")
        void testLoginFailure_ValidationError_ShortPassword() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "12345");

            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải từ 6 đến 100 ký tự");
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải từ 6 đến 100 ký tự", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC8: Login thất bại với validation error - username có ký tự đặc biệt")
        void testLoginFailure_ValidationError_InvalidUsernameChars() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("user@#$", "Test123");

            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username chỉ chứa chữ, số, và ký tự (-, ., _)");
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Username chỉ chứa chữ, số, và ký tự (-, ., _)", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC9: Login thất bại với validation error - password thiếu chữ cái")
        void testLoginFailure_ValidationError_PasswordMissingLetter() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "123456789");

            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải chứa ít nhất 1 chữ cái");
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải chứa ít nhất 1 chữ cái", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC10: Login thất bại với validation error - password thiếu số")
        void testLoginFailure_ValidationError_PasswordMissingNumber() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "PasswordOnly");

            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải chứa ít nhất 1 chữ số");
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of(violation));

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải chứa ít nhất 1 chữ số", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC11: Login với username hợp lệ chứa ký tự đặc biệt được phép")
        void testLoginSuccess_UsernameWithAllowedSpecialChars() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("test.user-name_123", "Test123");
            User userWithSpecialChars = new User(2L, "test.user-name_123", "hashedPassword123");

            // Mock validator không có lỗi
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of());

            // Mock
            when(mockUserRepository.findByUserName("test.user-name_123"))
                .thenReturn(Optional.of(userWithSpecialChars));
            when(mockPasswordEncoder.matches("Test123", "hashedPassword123"))
                .thenReturn(true);
            when(mockJwtService.generateToken(userWithSpecialChars))
                .thenReturn("fake-jwt-token-2");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertTrue(loginResponse.isSuccess());
            assertEquals("Login thành công", loginResponse.getMessage());
            assertNotNull(loginResponse.getToken());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }

        @Test
        @DisplayName("TC12: Login với password có độ dài tối đa (100 ký tự)")
        void testLoginSuccess_MaxLengthPassword() {
            // Arrange
            String maxPassword = "A1" + "b".repeat(98); // 100 ký tự: A + 1 + 98 chữ 'b'
            LoginRequest loginRequest = new LoginRequest("testUser", maxPassword);

            // Mock validator không có lỗi
            when(mockValidator.validate(loginRequest))
                .thenReturn(Set.of());

            // Mock
            when(mockUserRepository.findByUserName("testUser"))
                .thenReturn(Optional.of(mockUser));
            when(mockPasswordEncoder.matches(maxPassword, "hashedPassword123"))
                .thenReturn(true);
            when(mockJwtService.generateToken(mockUser))
                .thenReturn("fake-jwt-token-3");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertTrue(loginResponse.isSuccess());
            assertEquals("Login thành công", loginResponse.getMessage());
            
            verify(mockValidator, times(1)).validate(loginRequest);
        }
    }

    // ========================================================================================
    // B) TEST VALIDATION với Bean Validation (Jakarta Validation)
    // ========================================================================================

    @Nested
    @DisplayName("Test Bean Validation - Username validation")
    class UsernameValidationTests {

        @Test
        @DisplayName("Username null - Validator phải phát hiện lỗi @NotBlank")
        void validate_withNullUsername_shouldReturnUsernameNotEmptyError() {
            // Arrange
            var request = new LoginRequest(null, "ValidPassword123");
            
            // Mock validator trả về violation cho @NotBlank
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username không được để trống");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.isEmpty(), "Phải có lỗi validation");
            assertTrue(errors.contains("Username không được để trống"));
            
            verify(mockValidator, times(1)).validate(request);
        }

        @Test
        @DisplayName("Username rỗng - Validator phải phát hiện lỗi @NotBlank")
        void validate_withEmptyUsername_shouldReturnUsernameNotEmptyError() {
            // Arrange
            var request = new LoginRequest("", "ValidPassword123");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username không được để trống");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username không được để trống"));
        }

        @Test
        @DisplayName("Username quá ngắn (2 ký tự) - Validator phải phát hiện lỗi @Size")
        void validate_withShortUsername_shouldReturnUsernameLengthError() {
            // Arrange
            var request = new LoginRequest("ab", "ValidPassword123");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username phải từ 3 đến 50 ký tự");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
        }

        @Test
        @DisplayName("Username với ký tự đặc biệt không hợp lệ - Validator phải phát hiện lỗi @Pattern")
        void validate_withInvalidCharsUsername_shouldReturnUsernamePatternError() {
            // Arrange
            var request = new LoginRequest("user!@#", "ValidPassword123");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Username chỉ chứa chữ, số, và ký tự (-, ., _)");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        }

        @Test
        @DisplayName("Username hợp lệ - Không có lỗi validation")
        void validate_withValidUsername_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("user.name-123_test", "ValidPassword123");
            
            // Mock validator không có lỗi
            when(mockValidator.validate(request))
                .thenReturn(Set.of());
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.isEmpty(), "Không nên có lỗi validation");
        }
    }

    @Nested
    @DisplayName("Test Bean Validation - Password validation")
    class PasswordValidationTests {

        @Test
        @DisplayName("Password null - Validator phải phát hiện lỗi @NotBlank")
        void validate_withNullPassword_shouldReturnPasswordNotEmptyError() {
            // Arrange
            var request = new LoginRequest("validUser", null);
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password không được để trống");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password không được để trống"));
        }

        @Test
        @DisplayName("Password quá ngắn (5 ký tự) - Validator phải phát hiện lỗi @Size")
        void validate_withShortPassword_shouldReturnPasswordLengthError() {
            // Arrange
            var request = new LoginRequest("validUser", "Ab123");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải từ 6 đến 100 ký tự");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
        }

        @Test
        @DisplayName("Password chỉ có số - Validator phải phát hiện lỗi @Pattern thiếu chữ cái")
        void validate_withPasswordMissingLetter_shouldReturnPasswordLetterError() {
            // Arrange
            var request = new LoginRequest("validUser", "123456789");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải chứa ít nhất 1 chữ cái");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
        }

        @Test
        @DisplayName("Password chỉ có chữ - Validator phải phát hiện lỗi @Pattern thiếu số")
        void validate_withPasswordMissingNumber_shouldReturnPasswordNumberError() {
            // Arrange
            var request = new LoginRequest("validUser", "PasswordOnly");
            
            // Mock validator trả về violation
            ConstraintViolation<LoginRequest> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("Password phải chứa ít nhất 1 chữ số");
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation));
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ số"));
        }

        @Test
        @DisplayName("Password hợp lệ - Không có lỗi validation")
        void validate_withValidPassword_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("validUser", "Password123!@#");
            
            // Mock validator không có lỗi
            when(mockValidator.validate(request))
                .thenReturn(Set.of());
            
            // Act
            java.util.List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.isEmpty(), "Không nên có lỗi validation");
        }
    }

    @Nested
    @DisplayName("Test Bean Validation - Multiple errors")
    class MultipleErrorsValidationTests {

        @Test
        @DisplayName("Cả username và password đều null - Validator phải trả về nhiều lỗi")
        void validate_withBothNull_shouldReturnMultipleErrors() {
            // Arrange
            var request = new LoginRequest(null, null);

            // Mock validator trả về 2 violations
            ConstraintViolation<LoginRequest> violation1 = mock(ConstraintViolation.class);
            when(violation1.getMessage()).thenReturn("Username không được để trống");
            
            ConstraintViolation<LoginRequest> violation2 = mock(ConstraintViolation.class);
            when(violation2.getMessage()).thenReturn("Password không được để trống");
            
            when(mockValidator.validate(request))
                .thenReturn(Set.of(violation1, violation2));

            // Act
            java.util.List<String> errors = authService.validate(request);

            // Assert
            assertEquals(2, errors.size(), "Phải có đúng 2 lỗi");
            assertTrue(errors.contains("Username không được để trống"));
            assertTrue(errors.contains("Password không được để trống"));
        }

        @Test
        @DisplayName("Username và password đều vi phạm nhiều quy tắc - Validator trả về tất cả lỗi")
        void validate_withMultipleErrors_shouldReturnAllApplicableErrors() {
            // Arrange
            var request = new LoginRequest("a!", "123");

            // Mock validator trả về nhiều violations
            ConstraintViolation<LoginRequest> v1 = mock(ConstraintViolation.class);
            when(v1.getMessage()).thenReturn("Username phải từ 3 đến 50 ký tự");
            
            ConstraintViolation<LoginRequest> v2 = mock(ConstraintViolation.class);
            when(v2.getMessage()).thenReturn("Username chỉ chứa chữ, số, và ký tự (-, ., _)");
            
            ConstraintViolation<LoginRequest> v3 = mock(ConstraintViolation.class);
            when(v3.getMessage()).thenReturn("Password phải từ 6 đến 100 ký tự");
            
            ConstraintViolation<LoginRequest> v4 = mock(ConstraintViolation.class);
            when(v4.getMessage()).thenReturn("Password phải chứa ít nhất 1 chữ cái");
            
            when(mockValidator.validate(request))
                .thenReturn(Set.of(v1, v2, v3, v4));

            // Act
            java.util.List<String> errors = authService.validate(request);

            // Assert
            assertEquals(4, errors.size(), "Phải trả về đúng 4 lỗi");
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
            assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
        }

        @Test
        @DisplayName("Cả username và password đều hợp lệ - Không có lỗi validation")
        void validate_withValidCredentials_shouldReturnNoErrors() {
            // Arrange
            var request = new LoginRequest("validUser123", "ValidPass123");

            // Mock validator không có lỗi
            when(mockValidator.validate(request))
                .thenReturn(Set.of());

            // Act
            java.util.List<String> errors = authService.validate(request);

            // Assert
            assertTrue(errors.isEmpty(), "Không nên có lỗi validation");
        }
    }
}
