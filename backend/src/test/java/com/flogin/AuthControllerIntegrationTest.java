package com.flogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flogin.controller.AuthController;
import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.dto.UserDtos.UserDto;
import com.flogin.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthControllerIntegrationTest - Integration Test cho Auth API
 * Sử dụng @WebMvcTest để test các endpoint của AuthController
 * MockMvc cho phép test HTTP requests mà không cần start toàn bộ server
 */
@WebMvcTest(AuthController.class)
@DisplayName("Login API Integration Tests")
public class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private AuthService authService;

    /**
     * Test Group A: Test POST /api/auth/login endpoint (3 điểm)
     * Bao gồm các test case:
     * 1. Login thành công với credentials hợp lệ
     * 2. Login thất bại với username không tồn tại
     * 3. Login thất bại với password sai
     * 4. Login thất bại với username trống
     * 5. Login thất bại với password trống
     * 6. Login thất bại với username quá ngắn
     * 7. Login thất bại với password quá ngắn
     * 8. Login thất bại với username chứa ký tự đặc biệt không hợp lệ
     * 9. Login thất bại với password không chứa chữ cái
     * 10. Login thất bại với password không chứa số
     */
    @Nested
    @DisplayName("A) Test POST /api/auth/login endpoint - 3 điểm")
    class LoginEndpointTests {
        
        @Test
        @DisplayName("1. Login thành công với credentials hợp lệ")
        void testLoginSuccess() throws Exception {
            // Arrange: Chuẩn bị dữ liệu test
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(
                true, 
                "Login thành công", 
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token123",
                userDto
            );
            
            // Mock service trả về response thành công
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Thực hiện request và verify kết quả
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk()) // Expect 200 OK
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login thành công"))
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token123"))
                    .andExpect(jsonPath("$.user").exists())
                    .andExpect(jsonPath("$.user.userName").value("testuser"))
                    .andExpect(jsonPath("$.user.email").value("testuser@example.com"));
        }

        @Test
        @DisplayName("2. Login thất bại - Username không tồn tại")
        void testLoginFailure_UserNotFound() throws Exception {
            // Arrange: User không tồn tại trong database
            LoginRequest request = new LoginRequest("nonexistuser", "Test123");
            LoginResponse mockResponse = new LoginResponse(
                false, 
                "Login thất bại với user name không tồn tại"
            );
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Expect 401 Unauthorized
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized()) // Expect 401
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Login thất bại với user name không tồn tại"))
                    .andExpect(jsonPath("$.token").doesNotExist())
                    .andExpect(jsonPath("$.user").doesNotExist());
        }

        @Test
        @DisplayName("3. Login thất bại - Password sai")
        void testLoginFailure_WrongPassword() throws Exception {
            // Arrange: User tồn tại nhưng password sai
            LoginRequest request = new LoginRequest("testuser", "WrongPass123");
            LoginResponse mockResponse = new LoginResponse(
                false, 
                "Login với password sai"
            );
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Expect 401 Unauthorized
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Login với password sai"))
                    .andExpect(jsonPath("$.token").doesNotExist())
                    .andExpect(jsonPath("$.user").doesNotExist());
        }

        @Test
        @DisplayName("4. Login thất bại - Username trống")
        void testLoginFailure_EmptyUsername() throws Exception {
            // Arrange: Username trống vi phạm @NotBlank validation
            LoginRequest request = new LoginRequest("", "Test123");
            
            // Act & Assert: Expect 400 Bad Request do validation error
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest()); // Expect 400 Bad Request
        }

        @Test
        @DisplayName("5. Login thất bại - Password trống")
        void testLoginFailure_EmptyPassword() throws Exception {
            // Arrange: Password trống vi phạm @NotBlank validation
            LoginRequest request = new LoginRequest("testuser", "");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("6. Login thất bại - Username quá ngắn (< 3 ký tự)")
        void testLoginFailure_UsernameTooShort() throws Exception {
            // Arrange: Username chỉ có 2 ký tự vi phạm @Size(min = 3)
            LoginRequest request = new LoginRequest("ab", "Test123");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("7. Login thất bại - Password quá ngắn (< 6 ký tự)")
        void testLoginFailure_PasswordTooShort() throws Exception {
            // Arrange: Password chỉ có 5 ký tự vi phạm @Size(min = 6)
            LoginRequest request = new LoginRequest("testuser", "Te123");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("8. Login thất bại - Username chứa ký tự đặc biệt không hợp lệ")
        void testLoginFailure_UsernameInvalidCharacters() throws Exception {
            // Arrange: Username chứa ký tự @ không hợp lệ
            LoginRequest request = new LoginRequest("test@user!", "Test123");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("9. Login thất bại - Password không chứa chữ cái")
        void testLoginFailure_PasswordNoLetters() throws Exception {
            // Arrange: Password chỉ có số, không có chữ cái
            LoginRequest request = new LoginRequest("testuser", "123456");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("10. Login thất bại - Password không chứa số")
        void testLoginFailure_PasswordNoDigits() throws Exception {
            // Arrange: Password chỉ có chữ cái, không có số
            LoginRequest request = new LoginRequest("testuser", "TestPassword");
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("11. Login thất bại - Request body trống")
        void testLoginFailure_EmptyRequestBody() throws Exception {
            // Arrange: Gửi request không có body
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("12. Login thất bại - Request body không phải JSON hợp lệ")
        void testLoginFailure_InvalidJson() throws Exception {
            // Arrange: Gửi JSON không hợp lệ
            
            // Act & Assert: Expect 400 Bad Request
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid json}"))
                    .andExpect(status().isBadRequest());
        }
    }

    /**
     * Test Group B: Test response structure và status codes (1 điểm)
     * Kiểm tra cấu trúc response trả về có đúng format không
     * Kiểm tra các HTTP status codes phù hợp với từng tình huống
     */
    @Nested
    @DisplayName("B) Test Response Structure và Status Codes - 1 điểm")
    class ResponseStructureTests {
        
        @Test
        @DisplayName("1. Response structure có đầy đủ các field khi login thành công")
        void testSuccessResponseStructure() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(
                true, 
                "Login thành công", 
                "token.jwt.here",
                userDto
            );
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify response có đầy đủ field
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").isBoolean())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isString())
                    .andExpect(jsonPath("$.user").exists())
                    .andExpect(jsonPath("$.user.userName").exists())
                    .andExpect(jsonPath("$.user.email").exists());
        }

        @Test
        @DisplayName("2. Response structure khi login thất bại (không có token)")
        void testFailureResponseStructure() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("wronguser", "Test123");
            LoginResponse mockResponse = new LoginResponse(
                false, 
                "Login thất bại với user name không tồn tại"
            );
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify response không có token và user khi thất bại
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.token").doesNotExist()) // Token không tồn tại khi fail
                    .andExpect(jsonPath("$.user").doesNotExist()); // User cũng không tồn tại khi fail
        }

        @Test
        @DisplayName("3. Status code 200 OK khi login thành công")
        void testStatusCode_200_OnSuccess() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123");
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify status code = 200
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(status().is(200));
        }

        @Test
        @DisplayName("4. Status code 401 Unauthorized khi credentials sai")
        void testStatusCode_401_OnUnauthorized() throws Exception {
            // Arrange: Password hợp lệ về format nhưng sai credentials
            LoginRequest request = new LoginRequest("testuser", "WrongPass123");
            LoginResponse mockResponse = new LoginResponse(false, "Login với password sai");
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify status code = 401
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(status().is(401));
        }

        @Test
        @DisplayName("5. Status code 400 Bad Request khi validation fail")
        void testStatusCode_400_OnValidationError() throws Exception {
            // Arrange: Data không hợp lệ
            LoginRequest request = new LoginRequest("", ""); // Username và password trống
            
            // Act & Assert: Verify status code = 400
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("6. Response Content-Type là application/json")
        void testResponseContentType() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify Content-Type header
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    /**
     * Test Group C: Test CORS và Headers (1 điểm)
     * Kiểm tra CORS configuration có đúng không
     * Kiểm tra các HTTP headers cần thiết
     */
    @Nested
    @DisplayName("C) Test CORS và Headers - 1 điểm")
    class CorsAndHeadersTests {
        
        @Test
        @DisplayName("1. CORS - Access-Control-Allow-Origin header có tồn tại")
        void testCors_AllowOriginHeader() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify CORS header
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Origin", "http://localhost:3000") // Giả lập request từ frontend
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("2. CORS - Cho phép tất cả origins (*)")
        void testCors_AllowAllOrigins() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify origin được accept
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Origin", "http://localhost:3000")
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));
        }

        @Test
        @DisplayName("3. CORS - Preflight request (OPTIONS) được xử lý")
        void testCors_PreflightRequest() throws Exception {
            // Act & Assert: Test OPTIONS request (CORS preflight)
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .options("/api/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("4. Request accept header - application/json")
        void testAcceptHeader_ApplicationJson() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify server accept JSON
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON) // Client yêu cầu JSON response
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("5. Request với Content-Type sai bị reject")
        void testInvalidContentType_Rejected() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            
            // Act & Assert: Gửi request với Content-Type text/plain
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.TEXT_PLAIN) // Content-Type sai
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnsupportedMediaType()); // Expect 415
        }

        @Test
        @DisplayName("6. Response headers chứa thông tin cần thiết")
        void testResponseHeaders() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "Test123");
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Login thành công", "token123", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Verify các headers quan trọng
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Type"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("7. Test endpoint health check")
        void testHealthCheckEndpoint() throws Exception {
            // Act & Assert: Test health check endpoint
            mockMvc.perform(get("/api/auth/health")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Auth service is running"));
        }
    }
}
