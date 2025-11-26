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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * AuthControllerMockTest - Backend Mocking Tests (2.5 điểm)
 * 
 * Mục đích: Test controller với mocked dependencies
 * - Mock AuthService với @MockitoBean
 * - Test controller với mocked service
 * - Verify mock interactions
 */
@WebMvcTest(AuthController.class)
@DisplayName("Backend Mocking - AuthController Mock Tests (2.5 điểm)")
public class AuthControllerMockTest {
    
    @Autowired
    private MockMvc mockMvc;

    /**
     * a) Mock AuthService với @MockitoBean (1 điểm)
     * @MockitoBean tạo mock object và inject vào Spring context
     */
    @MockitoBean
    private AuthService authService;

    /**
     * A) Test Controller với Mocked Service - Success Cases (1 điểm)
     * Test các trường hợp thành công với mocked service
     */
    @Nested
    @DisplayName("A) Mock AuthService và Test Controller với Mocked Service - Success (1 điểm)")
    class MockedServiceSuccessTests {
        
        @Test
        @DisplayName("1. Mock: Controller với mocked service - Login thành công")
        void testLoginWithMockedService_Success() throws Exception {
            // Arrange: Mock response từ AuthService
            UserDto userDto = new UserDto("testuser", "testuser@example.com");
            LoginResponse mockResponse = new LoginResponse(
                true, 
                "Login thành công", 
                "mock-jwt-token-12345",
                userDto
            );
            
            // Mock behavior: Khi gọi authenticate với bất kỳ LoginRequest nào -> trả về mockResponse
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert: Thực hiện POST request và verify response
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"testuser\",\"password\":\"Test123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login thành công"))
                    .andExpect(jsonPath("$.token").value("mock-jwt-token-12345"))
                    .andExpect(jsonPath("$.user.userName").value("testuser"))
                    .andExpect(jsonPath("$.user.email").value("testuser@example.com"));
            

            // Verify rằng authService.authenticate() được gọi đúng 1 lần
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
        }

        @Test
        @DisplayName("2. Mock: Controller với mocked service - Nhiều request liên tiếp")
        void testLoginWithMockedService_MultipleRequests() throws Exception {
            // Arrange: Mock service trả về response khác nhau cho mỗi user
            UserDto user1 = new UserDto("user1", "user1@example.com");
            UserDto user2 = new UserDto("user2", "user2@example.com");
            
            LoginResponse mockResponse1 = new LoginResponse(true, "Login thành công", "token-user1", user1);
            LoginResponse mockResponse2 = new LoginResponse(true, "Login thành công", "token-user2", user2);
            
            // Mock trả về response khác nhau cho từng lần gọi
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse1)
                .thenReturn(mockResponse2);
            
            // Act & Assert: Request 1
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"user1\",\"password\":\"Pass123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token-user1"))
                    .andExpect(jsonPath("$.user.userName").value("user1"));
            
            // Act & Assert: Request 2
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"user2\",\"password\":\"Pass123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token-user2"))
                    .andExpect(jsonPath("$.user.userName").value("user2"));
            
            // Verify: authService.authenticate() được gọi 2 lần
            verify(authService, times(2)).authenticate(any(LoginRequest.class));
        }

        @Test
        @DisplayName("3. Mock: Verify argument matcher - Kiểm tra argument cụ thể")
        void testLoginWithMockedService_VerifySpecificArgument() throws Exception {
            // Arrange
            UserDto userDto = new UserDto("admin", "admin@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Admin login", "admin-token", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"admin\",\"password\":\"Admin123\"}"))
                    .andExpect(status().isOk());
            
            // Verify: Kiểm tra authenticate được gọi với LoginRequest có userName cụ thể
            verify(authService, times(1)).authenticate(argThat(request -> 
                request.getUserName().equals("admin")
            ));
        }
    }

    /**
     * B) Test Controller với Mocked Service - Failure Cases
     * Test các trường hợp thất bại để verify mock hoạt động đúng
     */
    @Nested
    @DisplayName("B) Test Controller với Mocked Service - Failure Cases")
    class MockedServiceFailureTests {
        
        @Test
        @DisplayName("4. Mock: Login thất bại - Username không tồn tại")
        void testLoginWithMockedService_UserNotFound() throws Exception {
            // Arrange: Mock service trả về failure response
            LoginResponse mockResponse = new LoginResponse(
                false, 
                "Login thất bại với user name không tồn tại"
            );
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"nonexistent\",\"password\":\"Pass123\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Login thất bại với user name không tồn tại"))
                    .andExpect(jsonPath("$.token").doesNotExist())
                    .andExpect(jsonPath("$.user").doesNotExist());
            
            // Verify interactions
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
        }

        @Test
        @DisplayName("5. Mock: Login thất bại - Password sai")
        void testLoginWithMockedService_WrongPassword() throws Exception {
            // Arrange: Mock service trả về password sai
            LoginResponse mockResponse = new LoginResponse(false, "Login với password sai");
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"testuser\",\"password\":\"WrongPass123\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Login với password sai"));
            
            // Verify interactions
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
        }
    }

    /**
     * C) Verify Mock Interactions - Chi tiết (0.5 điểm)
     * Test verify các interactions với mock objects
     */
    @Nested
    @DisplayName("C) Verify Mock Interactions - Chi tiết (0.5 điểm)")
    class VerifyMockInteractionsTests {
        
        @Test
        @DisplayName("6. Verify: Mock được gọi đúng 1 lần với times(1)")
        void testVerify_MockCalledExactlyOnce() throws Exception {
            // Arrange
            UserDto userDto = new UserDto("testuser", "test@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Success", "token", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"testuser\",\"password\":\"Pass123\"}"))
                    .andExpect(status().isOk());
            
            // c) Verify: Kiểm tra mock được gọi đúng 1 lần
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
            
            // Verify không có interaction nào khác
            verifyNoMoreInteractions(authService);
        }

        @Test
        @DisplayName("7. Verify: Mock không được gọi khi validation fail")
        void testVerify_MockNotCalledWhenValidationFails() throws Exception {
            // Arrange: Không cần mock vì validation sẽ fail trước khi gọi service
            
            // Act: Gửi request với username trống (validation fail)
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"\",\"password\":\"Pass123\"}"))
                    .andExpect(status().isBadRequest());
            
            // c) Verify: authService.authenticate() KHÔNG được gọi vì validation fail
            verify(authService, never()).authenticate(any(LoginRequest.class));
            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("8. Verify: Mock được gọi đúng số lần với atLeast/atMost")
        void testVerify_MockCalledWithAtLeastAtMost() throws Exception {
            // Arrange
            UserDto userDto = new UserDto("testuser", "test@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Success", "token", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act: Gọi 3 lần
            for (int i = 0; i < 3; i++) {
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"testuser\",\"password\":\"Pass123\"}"))
                        .andExpect(status().isOk());
            }
            
            // c) Verify: Kiểm tra mock được gọi ít nhất 2 lần và nhiều nhất 5 lần
            verify(authService, atLeast(2)).authenticate(any(LoginRequest.class));
            verify(authService, atMost(5)).authenticate(any(LoginRequest.class));
            verify(authService, times(3)).authenticate(any(LoginRequest.class));
        }

        @Test
        @DisplayName("9. Verify: Kiểm tra thứ tự gọi mock với InOrder")
        void testVerify_MockCallOrder() throws Exception {
            // Arrange
            UserDto user1 = new UserDto("user1", "user1@example.com");
            UserDto user2 = new UserDto("user2", "user2@example.com");
            
            LoginResponse response1 = new LoginResponse(true, "Success", "token1", user1);
            LoginResponse response2 = new LoginResponse(true, "Success", "token2", user2);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(response1)
                .thenReturn(response2);
            
            // Act: Gọi theo thứ tự
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"user1\",\"password\":\"Pass123\"}"));
            
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"user2\",\"password\":\"Pass123\"}"));
            
            // c) Verify: Kiểm tra thứ tự gọi
            var inOrder = inOrder(authService);
            inOrder.verify(authService).authenticate(argThat(req -> req.getUserName().equals("user1")));
            inOrder.verify(authService).authenticate(argThat(req -> req.getUserName().equals("user2")));
        }

        @Test
        @DisplayName("10. Verify: Reset mock và verify lại")
        void testVerify_ResetMock() throws Exception {
            // Arrange
            UserDto userDto = new UserDto("testuser", "test@example.com");
            LoginResponse mockResponse = new LoginResponse(true, "Success", "token", userDto);
            
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act: Gọi lần 1
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"testuser\",\"password\":\"Pass123\"}"));
            
            // Verify lần 1
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
            
            // Reset mock
            reset(authService);
            
            // Mock lại
            when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);
            
            // Act: Gọi lần 2
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userName\":\"testuser\",\"password\":\"Pass123\"}"));
            
            // c) Verify: Sau khi reset, counter được đếm lại từ đầu
            verify(authService, times(1)).authenticate(any(LoginRequest.class));
        }
    }

}
