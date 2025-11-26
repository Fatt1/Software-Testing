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
