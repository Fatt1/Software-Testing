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
 * AuthController - REST API Controller cho authentication
 * Xử lý các request liên quan đến đăng nhập, đăng ký
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login - Endpoint đăng nhập
     * @param request LoginRequest chứa userName và password
     * @return LoginResponse với token nếu thành công
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        
        // Nếu login thành công, trả về 200 OK
        // Nếu login thất bại, trả về 401 Unauthorized
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


}
