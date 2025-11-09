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
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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


    // --- Các Test Case cho Username (Quy tắc 1-4) ---

    @Test
    @DisplayName("Nên báo lỗi 'không được để trống' khi username là null")
    void validate_withNullUsername_shouldReturnUsernameNotEmptyError() {
        var request = new LoginRequest(null, "ValidPassword123");
        List<String> errors = authService.validate(request);
        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("Username không được để trống"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'không được để trống' khi username chỉ có khoảng trắng")
    void validate_withWhitespaceUsername_shouldReturnUsernameNotEmptyError() {
        var request = new LoginRequest("   ", "ValidPassword123");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Username không được để trống"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'độ dài' khi username quá ngắn (dưới 3)")
    void validate_withShortUsername_shouldReturnUsernameLengthError() {
        var request = new LoginRequest("ab", "ValidPassword123");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'độ dài' khi username quá dài (trên 50)")
    void validate_withLongUsername_shouldReturnUsernameLengthError() {
        String longUsername = "a".repeat(51); // Tạo chuỗi 51 ký tự 'a'
        var request = new LoginRequest(longUsername, "ValidPassword123");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'ký tự' khi username chứa ký tự không hợp lệ")
    void validate_withInvalidCharsUsername_shouldReturnUsernamePatternError() {
        var request = new LoginRequest("user!@#", "ValidPassword123");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
    }

    // --- Các Test Case cho Password (Quy tắc 5-9) ---

    @Test
    @DisplayName("Nên báo lỗi 'không được để trống' khi password là null")
    void validate_withNullPassword_shouldReturnPasswordNotEmptyError() {
        var request = new LoginRequest("validUser", null);
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password không được để trống"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'không được để trống' khi password rỗng")
    void validate_withEmptyPassword_shouldReturnPasswordNotEmptyError() {
        var request = new LoginRequest("validUser", "");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password không được để trống"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'độ dài' khi password quá ngắn (dưới 6)")
    void validate_withShortPassword_shouldReturnPasswordLengthError() {
        var request = new LoginRequest("validUser", "12345"); // 5 ký tự
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'độ dài' khi password quá dài (trên 100)")
    void validate_withLongPassword_shouldReturnPasswordLengthError() {
        String longPassword = "a".repeat(101); // 101 ký tự 'a'
        var request = new LoginRequest("validUser", longPassword);
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'thiếu chữ cái' khi password chỉ chứa số")
    void validate_withPasswordMissingLetter_shouldReturnPasswordLetterError() {
        var request = new LoginRequest("validUser", "123456789");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
    }

    @Test
    @DisplayName("Nên báo lỗi 'thiếu chữ số' khi password chỉ chứa chữ")
    void validate_withPasswordMissingNumber_shouldReturnPasswordNumberError() {
        var request = new LoginRequest("validUser", "PasswordOnly");
        List<String> errors = authService.validate(request);
        assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ số"));
    }

    // --- Test Case tổng hợp nhiều lỗi ---

    @Test
    @DisplayName("Nên trả về nhiều lỗi khi cả username và password đều vi phạm")
    void validate_withMultipleErrors_shouldReturnAllApplicableErrors() {
        // Username: quá ngắn (lỗi 1), chứa ký tự đặc biệt (lỗi 2)
        // Password: quá ngắn (lỗi 3), chỉ có số (lỗi 4)
        var request = new LoginRequest("a!", "123");

        List<String> errors = authService.validate(request);

        // Kiểm tra số lượng lỗi
        assertEquals(4, errors.size(), "Phải trả về đúng 4 lỗi");

        // Kiểm tra sự tồn tại của từng lỗi
        assertTrue(errors.contains("Username phải từ 3 đến 50 ký tự"));
        assertTrue(errors.contains("Username chỉ chứa chữ, số, và ký tự (-, ., _)"));
        assertTrue(errors.contains("Password phải từ 6 đến 100 ký tự"));
        assertTrue(errors.contains("Password phải chứa ít nhất 1 chữ cái"));
    }


}
