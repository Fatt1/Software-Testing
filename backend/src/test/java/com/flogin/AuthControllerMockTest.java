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


@WebMvcTest(AuthController.class)
@DisplayName("Backend Mocking - AuthController Mock Tests")
public class AuthControllerMockTest {
    
    @Autowired
    private MockMvc mockMvc;

    /**
     * a) Mock AuthService với @MockitoBean (1 điểm)
     * @MockitoBean tạo mock object và inject vào Spring context
     */
    @MockitoBean
    private AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * A) Test Controller với Mocked Service - Success Cases
     * Test các trường hợp thành công với mocked service
     */
    @Nested
    @DisplayName("A) Mock AuthService và Test Controller với Mocked Service - Success (1 điểm)")
    class MockedServiceSuccessTests {

        @Test
        @DisplayName("1. Mock: Controller với mocked service - Login thành công")
        void testLoginWithMockedService_Success() throws Exception {
            //Arrange
            LoginRequest request = new LoginRequest("testUser", "test12345");
            UserDto userDto = new UserDto("testUser", "testUser@gmail.com");
            LoginResponse response = new LoginResponse(true, "Login thành công", "jwt-fake-token", userDto);

            when(authService.authenticate(any(LoginRequest.class)))
                    .thenReturn(response);

            //Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.user").exists())
                    .andExpect(jsonPath("$.user.userName").value("testUser"));

            // Verify
            verify(authService, times(1)).authenticate(any(LoginRequest.class));

        }


        @Test
        @DisplayName("2. Mock: Verify argument matcher - Kiểm tra argument cụ thể")
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
            verify(authService, times(1)).authenticate(argThat(request -> "admin".equals(request.getUserName())));
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
        @DisplayName("3. Mock: Login thất bại - Username không tồn tại")
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
        @DisplayName("4. Mock: Login thất bại - Password sai")
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
        @DisplayName("5. Verify: Mock được gọi đúng 1 lần với times(1)")
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
        @DisplayName("6. Verify: Mock không được gọi khi validation fail")
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



    }

}
