package com.flogin.controller;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - REST API Controller for Authentication
 * 
 * <p>Controller này xử lý tất cả các request liên quan đến authentication,
 * bao gồm đăng nhập, đăng ký và xác thực người dùng.</p>
 * 
 * <p>Các endpoint chính:</p>
 * <ul>
 *   <li>POST /api/auth/login - Đăng nhập và nhận JWT token</li>
 *   <li>POST /api/auth/register - Đăng ký tài khoản mới</li>
 * </ul>
 * 
 * <p>Security: Sử dụng JWT (JSON Web Token) cho authentication.
 * CORS được enable cho tất cả origins để hỗ trợ frontend development.</p>
 * 
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    
    /**
     * AuthService instance được inject bởi Spring để xử lý business logic
     * của authentication process
     */
    @Autowired
    private AuthService authService;

    /**
     * Endpoint xử lý login request
     * 
     * <p>Nhận username và password từ client, thực hiện validation,
     * authenticate với database và trả về JWT token nếu thành công.</p>
     * 
     * <p><b>Request Body Example:</b></p>
     * <pre>
     * {
     *   "userName": "admin",
     *   "password": "admin123"
     * }
     * </pre>
     * 
     * <p><b>Success Response Example (200 OK):</b></p>
     * <pre>
     * {
     *   "success": true,
     *   "message": "Login successful",
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "userName": "admin"
     * }
     * </pre>
     * 
     * <p><b>Error Response Example (401 Unauthorized):</b></p>
     * <pre>
     * {
     *   "success": false,
     *   "message": "Invalid username or password",
     *   "token": null,
     *   "userName": null
     * }
     * </pre>
     * 
     * @param request LoginRequest object chứa userName và password, được validate bởi @Valid
     * @return ResponseEntity chứa LoginResponse với HTTP status code tương ứng
     *         - 200 OK nếu authentication thành công
     *         - 401 Unauthorized nếu username/password không đúng
     * @throws org.springframework.web.bind.MethodArgumentNotValidException nếu validation fails
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Gọi AuthService để xử lý authentication logic
        LoginResponse response = authService.authenticate(request);
        
        // Kiểm tra kết quả authentication và trả về response code phù hợp
        // Nếu login thành công, trả về 200 OK với JWT token
        // Nếu login thất bại, trả về 401 Unauthorized với error message
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


}
