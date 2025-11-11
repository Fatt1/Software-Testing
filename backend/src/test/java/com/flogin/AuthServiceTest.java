package com.flogin;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.entity.User;
import com.flogin.repository.interfaces.JwtService;
import com.flogin.repository.interfaces.PasswordEncoder;
import com.flogin.repository.interfaces.UserRepository;
import com.flogin.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * AuthService Unit Test
 * Coverage >= 85%
 * 
 * Test scenarios:
 * - authenticate() với các trường hợp: success, user không tồn tại, password sai, validation errors
 * - validate() methods riêng lẻ cho username và password
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Test")
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
            verify(mockUserRepository, times(1)).findByUserName("testUser");
            verify(mockPasswordEncoder, times(1)).matches("Test123", "hashedPassword123");
            verify(mockJwtService, times(1)).generateToken(mockUser);
        }

        @Test
        @DisplayName("TC2: Login thất bại khi username không tồn tại")
        void testLoginFailure_UserNotFound() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("nonExistentUser", "Test123");

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
            verify(mockUserRepository, times(1)).findByUserName("nonExistentUser");
            verify(mockPasswordEncoder, never()).matches(anyString(), anyString());
            verify(mockJwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("TC3: Login thất bại khi password sai")
        void testLoginFailure_WrongPassword() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "WrongPassword123");

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
            verify(mockUserRepository, times(1)).findByUserName("testUser");
            verify(mockPasswordEncoder, times(1)).matches("WrongPassword123", "hashedPassword123");
            verify(mockJwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("TC4: Login thất bại với validation error - username null")
        void testLoginFailure_ValidationError_NullUsername() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest(null, "Test123");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess(), "Login phải thất bại do validation");
            assertEquals("Username không được để trống", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            // Verify không có interaction với dependencies
            verify(mockUserRepository, never()).findByUserName(anyString());
            verify(mockPasswordEncoder, never()).matches(anyString(), anyString());
            verify(mockJwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("TC5: Login thất bại với validation error - password null")
        void testLoginFailure_ValidationError_NullPassword() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", null);

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess(), "Login phải thất bại do validation");
            assertEquals("Password không được để trống", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
            
            // Verify không có interaction với dependencies
            verify(mockUserRepository, never()).findByUserName(anyString());
        }

        @Test
        @DisplayName("TC6: Login thất bại với validation error - username quá ngắn")
        void testLoginFailure_ValidationError_ShortUsername() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("ab", "Test123");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Username phải từ 3 đến 50 ký tự", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
        }

        @Test
        @DisplayName("TC7: Login thất bại với validation error - password quá ngắn")
        void testLoginFailure_ValidationError_ShortPassword() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "12345");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải từ 6 đến 100 ký tự", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
        }

        @Test
        @DisplayName("TC8: Login thất bại với validation error - username có ký tự đặc biệt")
        void testLoginFailure_ValidationError_InvalidUsernameChars() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("user@#$", "Test123");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Username chỉ chứa chữ, số, và ký tự (-, ., _)", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
        }

        @Test
        @DisplayName("TC9: Login thất bại với validation error - password thiếu chữ cái")
        void testLoginFailure_ValidationError_PasswordMissingLetter() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "123456789");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải chứa ít nhất 1 chữ cái", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
        }

        @Test
        @DisplayName("TC10: Login thất bại với validation error - password thiếu số")
        void testLoginFailure_ValidationError_PasswordMissingNumber() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("testUser", "PasswordOnly");

            // Act
            LoginResponse loginResponse = authService.authenticate(loginRequest);

            // Assert
            assertFalse(loginResponse.isSuccess());
            assertEquals("Password phải chứa ít nhất 1 chữ số", loginResponse.getMessage());
            assertNull(loginResponse.getToken());
        }

        @Test
        @DisplayName("TC11: Login với username hợp lệ chứa ký tự đặc biệt được phép")
        void testLoginSuccess_UsernameWithAllowedSpecialChars() {
            // Arrange
            LoginRequest loginRequest = new LoginRequest("test.user-name_123", "Test123");
            User userWithSpecialChars = new User(2L, "test.user-name_123", "hashedPassword123");

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
        }

        @Test
        @DisplayName("TC12: Login với password có độ dài tối đa (100 ký tự)")
        void testLoginSuccess_MaxLengthPassword() {
            // Arrange
            String maxPassword = "A1" + "b".repeat(98); // 100 ký tự: A + 1 + 98 chữ 'b'
            LoginRequest loginRequest = new LoginRequest("testUser", maxPassword);

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
        }
    }

    // ========================================================================================
    // B) TEST VALIDATION METHODS riêng lẻ (1 điểm)
    // ========================================================================================

    @Nested
    @DisplayName("Test validate() method - Username validation")
    class UsernameValidationTests {

    @Nested
    @DisplayName("Test validate() method - Username validation")
    class UsernameValidationTests {

        @Test
        @DisplayName("Username null - Nên báo lỗi 'không được để trống'")
        void validate_withNullUsername_shouldReturnUsernameNotEmptyError() {
            // Arrange
            var request = new LoginRequest(null, "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.isEmpty(), "Phải có lỗi validation");
            assertTrue(errors.contains("Username không được để trống"));
        }

        @Test
        @DisplayName("Username rỗng - Nên báo lỗi 'không được để trống'")
        void validate_withEmptyUsername_shouldReturnUsernameNotEmptyError() {
            // Arrange
            var request = new LoginRequest("", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username không được để trống"));
        }

        @Test
        @DisplayName("Username chỉ có khoảng trắng - Nên báo lỗi 'không được để trống'")
        void validate_withWhitespaceUsername_shouldReturnUsernameNotEmptyError() {
            // Arrange
            var request = new LoginRequest("   ", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username không được để trống"));
        }

        @Test
        @DisplayName("Username quá ngắn (2 ký tự) - Nên báo lỗi độ dài")
        void validate_withShortUsername_shouldReturnUsernameLengthError() {
            // Arrange
            var request = new LoginRequest("ab", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
        }

        @Test
        @DisplayName("Username quá dài (51 ký tự) - Nên báo lỗi độ dài")
        void validate_withLongUsername_shouldReturnUsernameLengthError() {
            // Arrange
            String longUsername = "a".repeat(51);
            var request = new LoginRequest(longUsername, "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
        }

        @Test
        @DisplayName("Username với ký tự đặc biệt không hợp lệ - Nên báo lỗi pattern")
        void validate_withInvalidCharsUsername_shouldReturnUsernamePatternError() {
            // Arrange
            var request = new LoginRequest("user!@#", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        }

        @Test
        @DisplayName("Username với khoảng trắng - Nên báo lỗi pattern")
        void validate_withSpaceInUsername_shouldReturnUsernamePatternError() {
            // Arrange
            var request = new LoginRequest("user name", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        }

        @Test
        @DisplayName("Username hợp lệ với ký tự đặc biệt được phép - Không có lỗi")
        void validate_withValidUsernameSpecialChars_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("user.name-123_test", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert - Chỉ kiểm tra không có lỗi username
            assertFalse(errors.contains("Username không được để trống"));
            assertFalse(errors.contains("Username phải từ 3 đến 50 ký tự"));
            assertFalse(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        }

        @Test
        @DisplayName("Username đúng 3 ký tự (boundary) - Không có lỗi")
        void validate_withMinLengthUsername_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("abc", "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.contains("Username phải từ 3 đến 50 ký tự"));
        }

        @Test
        @DisplayName("Username đúng 50 ký tự (boundary) - Không có lỗi")
        void validate_withMaxLengthUsername_shouldReturnNoError() {
            // Arrange
            String maxUsername = "a".repeat(50);
            var request = new LoginRequest(maxUsername, "ValidPassword123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.contains("Username phải từ 3 đến 50 ký tự"));
        }
    }

    @Nested
    @DisplayName("Test validate() method - Password validation")
    class PasswordValidationTests {

        @Test
        @DisplayName("Password null - Nên báo lỗi 'không được để trống'")
        void validate_withNullPassword_shouldReturnPasswordNotEmptyError() {
            // Arrange
            var request = new LoginRequest("validUser", null);
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password không được để trống"));
        }

        @Test
        @DisplayName("Password rỗng - Nên báo lỗi 'không được để trống'")
        void validate_withEmptyPassword_shouldReturnPasswordNotEmptyError() {
            // Arrange
            var request = new LoginRequest("validUser", "");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password không được để trống"));
        }

        @Test
        @DisplayName("Password quá ngắn (5 ký tự) - Nên báo lỗi độ dài")
        void validate_withShortPassword_shouldReturnPasswordLengthError() {
            // Arrange
            var request = new LoginRequest("validUser", "Ab123");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
        }

        @Test
        @DisplayName("Password quá dài (101 ký tự) - Nên báo lỗi độ dài")
        void validate_withLongPassword_shouldReturnPasswordLengthError() {
            // Arrange
            String longPassword = "A1" + "b".repeat(99); // 101 ký tự
            var request = new LoginRequest("validUser", longPassword);
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
        }

        @Test
        @DisplayName("Password chỉ có số - Nên báo lỗi thiếu chữ cái")
        void validate_withPasswordMissingLetter_shouldReturnPasswordLetterError() {
            // Arrange
            var request = new LoginRequest("validUser", "123456789");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
        }

        @Test
        @DisplayName("Password chỉ có chữ - Nên báo lỗi thiếu số")
        void validate_withPasswordMissingNumber_shouldReturnPasswordNumberError() {
            // Arrange
            var request = new LoginRequest("validUser", "PasswordOnly");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ số"));
        }

        @Test
        @DisplayName("Password có ký tự đặc biệt nhưng thiếu số - Vẫn báo lỗi thiếu số")
        void validate_withPasswordSpecialCharsButMissingNumber_shouldReturnPasswordNumberError() {
            // Arrange
            var request = new LoginRequest("validUser", "Password!@#");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ số"));
        }

        @Test
        @DisplayName("Password hợp lệ với chữ, số và ký tự đặc biệt - Không có lỗi")
        void validate_withValidPasswordAllTypes_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("validUser", "Password123!@#");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert - Chỉ kiểm tra không có lỗi password
            assertFalse(errors.contains("Password không được để trống"));
            assertFalse(errors.contains("Password phải từ 6 đến 100 ký tự"));
            assertFalse(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
            assertFalse(errors.contains("Password phải chứa ít nhất 1 chữ số"));
        }

        @Test
        @DisplayName("Password đúng 6 ký tự (boundary) - Không có lỗi")
        void validate_withMinLengthPassword_shouldReturnNoError() {
            // Arrange
            var request = new LoginRequest("validUser", "Pass12");
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.contains("Password phải từ 6 đến 100 ký tự"));
        }

        @Test
        @DisplayName("Password đúng 100 ký tự (boundary) - Không có lỗi")
        void validate_withMaxLengthPassword_shouldReturnNoError() {
            // Arrange
            String maxPassword = "A1" + "b".repeat(98); // 100 ký tự
            var request = new LoginRequest("validUser", maxPassword);
            
            // Act
            List<String> errors = authService.validate(request);
            
            // Assert
            assertFalse(errors.contains("Password phải từ 6 đến 100 ký tự"));
        }
    }

    @Nested
    @DisplayName("Test validate() method - Multiple errors")
    class MultipleErrorsValidationTests {

        @Test
        @DisplayName("Cả username và password đều null - Nên trả về 2 lỗi")
        void validate_withBothNull_shouldReturnBothErrors() {
            // Arrange
            var request = new LoginRequest(null, null);

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertEquals(2, errors.size(), "Phải có đúng 2 lỗi");
            assertTrue(errors.contains("Username không được để trống"));
            assertTrue(errors.contains("Password không được để trống"));
        }

        @Test
        @DisplayName("Username và password đều vi phạm nhiều quy tắc - Nên trả về tất cả lỗi")
        void validate_withMultipleErrors_shouldReturnAllApplicableErrors() {
            // Arrange
            // Username: quá ngắn (2 ký tự) + chứa ký tự đặc biệt không hợp lệ
            // Password: quá ngắn (3 ký tự) + chỉ có số
            var request = new LoginRequest("a!", "123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertEquals(4, errors.size(), "Phải trả về đúng 4 lỗi");
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
            assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
        }

        @Test
        @DisplayName("Username hợp lệ, chỉ password vi phạm - Chỉ trả về lỗi password")
        void validate_withOnlyPasswordErrors_shouldReturnOnlyPasswordErrors() {
            // Arrange
            var request = new LoginRequest("validUser123", "12345");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertEquals(2, errors.size());
            assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
            assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
            // Không có lỗi username
            assertFalse(errors.stream().anyMatch(e -> e.contains("Username")));
        }

        @Test
        @DisplayName("Password hợp lệ, chỉ username vi phạm - Chỉ trả về lỗi username")
        void validate_withOnlyUsernameErrors_shouldReturnOnlyUsernameErrors() {
            // Arrange
            var request = new LoginRequest("ab", "ValidPass123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertEquals(1, errors.size());
            assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
            // Không có lỗi password
            assertFalse(errors.stream().anyMatch(e -> e.contains("Password")));
        }

        @Test
        @DisplayName("Cả username và password đều hợp lệ - Không có lỗi")
        void validate_withValidCredentials_shouldReturnNoErrors() {
            // Arrange
            var request = new LoginRequest("validUser123", "ValidPass123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertTrue(errors.isEmpty(), "Không nên có lỗi validation");
        }
    }

    // ========================================================================================
    // C) EDGE CASES & ADDITIONAL SCENARIOS để đạt coverage >= 85%
    // ========================================================================================

    @Nested
    @DisplayName("Edge cases and additional scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Username có dấu gạch ngang ở đầu - Hợp lệ")
        void validate_usernameStartsWithHyphen_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("-username", "Pass123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertFalse(errors.stream().anyMatch(e -> e.contains("Username chỉ chứa")));
        }

        @Test
        @DisplayName("Username có dấu chấm ở cuối - Hợp lệ")
        void validate_usernameEndsWithDot_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("username.", "Pass123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertFalse(errors.stream().anyMatch(e -> e.contains("Username chỉ chứa")));
        }

        @Test
        @DisplayName("Password với chữ hoa, chữ thường và số - Hợp lệ")
        void validate_passwordMixedCase_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("username", "PaSsWoRd123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("Password chỉ có 1 chữ cái và phần còn lại là số - Hợp lệ")
        void validate_passwordOneLetterRestNumbers_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("username", "a12345");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("Password chỉ có 1 số và phần còn lại là chữ - Hợp lệ")
        void validate_passwordOneNumberRestLetters_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("username", "abcdef1");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("Username với tất cả ký tự hợp lệ - Không có lỗi")
        void validate_usernameAllValidCharacters_shouldBeValid() {
            // Arrange
            var request = new LoginRequest("aA0-._", "Pass123");

            // Act
            List<String> errors = authService.validate(request);

            // Assert
            assertFalse(errors.stream().anyMatch(e -> e.contains("Username")));
        }
    }


}
