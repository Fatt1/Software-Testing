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

    @Nested
    @DisplayName("A) Test POST /api/auth/login endpoint")
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


    }


    @Nested
    @DisplayName("B) Test Response Structure và Status Codes")
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
                "token.jwt",
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
    @DisplayName("C) Test CORS và Headers ")
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

    }
}
