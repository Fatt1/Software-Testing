package com.flogin.dto.LoginDto;

import jakarta.validation.constraints.*;

/**
 * LoginRequest - Data Transfer Object for User Authentication
 * 
 * Purpose:
 * This DTO encapsulates user login credentials sent from the client to the server.
 * It serves as the request body for the POST /api/auth/login endpoint.
 * 
 * Design Pattern: DTO (Data Transfer Object)
 * - Separates API layer from domain model
 * - Provides validation at the entry point
 * - Prevents over-posting attacks
 * - Documents API contract
 * 
 * Jakarta Bean Validation:
 * This class uses Jakarta Bean Validation annotations to enforce business rules
 * at the API boundary, ensuring data integrity before processing.
 * 
 * Validation Rules:
 * 
 * Username Field:
 * - @NotBlank: Cannot be null, empty, or whitespace only
 * - @Size(min=3, max=50): Length must be between 3 and 50 characters
 * - @Pattern(regexp="^[a-zA-Z0-9_.-]+$"): Only alphanumeric and special chars (-, ., _)
 * 
 * Valid Examples:
 * - "admin" ✓
 * - "user_123" ✓
 * - "john.doe" ✓
 * 
 * Invalid Examples:
 * - "ab" ✗ (too short)
 * - "user@domain" ✗ (@ not allowed)
 * - "user name" ✗ (space not allowed)
 * - "" ✗ (empty string)
 * 
 * Password Field:
 * - @NotBlank: Cannot be null, empty, or whitespace only
 * - @Size(min=6, max=100): Length must be between 6 and 100 characters
 * - @Pattern(regexp=".*[a-zA-Z]+.*"): Must contain at least one letter
 * - @Pattern(regexp=".*[0-9]+.*"): Must contain at least one digit
 * 
 * Valid Examples:
 * - "password123" ✓
 * - "Test@123" ✓
 * - "myPass1" ✓
 * 
 * Invalid Examples:
 * - "12345" ✗ (no letters)
 * - "abcdef" ✗ (no digits)
 * - "pass1" ✗ (too short)
 * 
 * Security Considerations:
 * - Password is transmitted over HTTPS (required)
 * - Password is never logged or exposed in responses
 * - Password is hashed using BCrypt before storage
 * - Validation messages do not reveal security details
 * 
 * JSON Request Example:
 * <pre>
 * {
 *   "userName": "admin",
 *   "password": "admin123"
 * }
 * </pre>
 * 
 * Validation Error Response:
 * If validation fails, the API returns 400 BAD REQUEST with error details:
 * <pre>
 * {
 *   "message": "Validation failed",
 *   "errors": [
 *     "Username phải từ 3 đến 50 ký tự",
 *     "Password phải chứa ít nhất 1 chữ số"
 *   ]
 * }
 * </pre>
 * 
 * Usage in Controller:
 * <pre>
 * {@code
 * @PostMapping("/login")
 * public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
 *     // @Valid triggers validation
 *     // If validation fails, MethodArgumentNotValidException is thrown
 *     LoginResponse response = authService.authenticate(request);
 *     return ResponseEntity.ok(response);
 * }
 * }
 * </pre>
 * 
 * Immutability:
 * This class is effectively immutable (no setters) to prevent modification
 * after creation, ensuring request integrity throughout the request lifecycle.
 * 
 * @see LoginResponse
 * @see com.flogin.controller.AuthController#login(LoginRequest)
 * @see com.flogin.service.AuthService#authenticate(LoginRequest)
 */
public class LoginRequest {
    
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username chỉ chứa chữ, số, và ký tự (-, ., _)")
    private String userName;
    
    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6 đến 100 ký tự")
    @Pattern(regexp = ".*[a-zA-Z]+.*", message = "Password phải chứa ít nhất 1 chữ cái")
    @Pattern(regexp = ".*[0-9]+.*", message = "Password phải chứa ít nhất 1 chữ số")
    private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
