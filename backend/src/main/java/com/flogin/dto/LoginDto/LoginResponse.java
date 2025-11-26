package com.flogin.dto.LoginDto;

import com.flogin.dto.UserDtos.UserDto;

/**
 * LoginResponse - Data Transfer Object for Authentication Response
 * 
 * Purpose:
 * This DTO encapsulates the server's response after a login attempt.
 * It communicates authentication success/failure and provides necessary
 * data for the client to maintain the authenticated session.
 * 
 * Design Pattern: DTO (Data Transfer Object)
 * - Standardizes API response format
 * - Separates internal entities from API contracts
 * - Provides flexibility for response evolution
 * - Documents API response structure
 * 
 * Response Fields:
 * 
 * 1. success (boolean)
 *    - Indicates if authentication was successful
 *    - true: Login successful, token and user data included
 *    - false: Login failed, only error message included
 * 
 * 2. message (String)
 *    - Human-readable message describing the result
 *    - Success: "Login thành công" or "Login successful"
 *    - Failure: "Invalid credentials", "User not found", etc.
 *    - Used for displaying feedback to users
 * 
 * 3. token (String, optional)
 *    - JWT (JSON Web Token) for authenticated requests
 *    - Only present when success = true
 *    - Format: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 *    - Client must include in subsequent requests:
 *      Authorization: Bearer <token>
 * 
 * 4. user (UserDto, optional)
 *    - User information for the authenticated user
 *    - Only present when success = true
 *    - Contains: username, email
 *    - Used to display user profile in UI
 * 
 * Successful Login Response:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Login thành công",
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcwMDAwMDAwMH0.abc123",
 *   "user": {
 *     "userName": "admin",
 *     "email": "admin@example.com"
 *   }
 * }
 * </pre>
 * 
 * Failed Login Response:
 * <pre>
 * {
 *   "success": false,
 *   "message": "Invalid username or password",
 *   "token": null,
 *   "user": null
 * }
 * </pre>
 * 
 * Constructor Overloading:
 * This class provides multiple constructors for different scenarios:
 * 
 * 1. Full Constructor:
 *    new LoginResponse(true, "Success", "token123", userDto)
 *    Used for successful authentication with all data
 * 
 * 2. Token Constructor (backward compatibility):
 *    new LoginResponse(true, "Success", "token123")
 *    Used when user data is not needed immediately
 * 
 * 3. Simple Constructor (error cases):
 *    new LoginResponse(false, "Error message")
 *    Used for authentication failures
 * 
 * JWT Token Structure:
 * The token is a signed JWT containing:
 * - Header: Algorithm and token type
 * - Payload: User claims (username, roles, expiration)
 * - Signature: HMAC SHA256 signature for verification
 * 
 * Token Expiration:
 * - Tokens expire after 24 hours (configurable)
 * - Client should handle 401 Unauthorized and redirect to login
 * - Refresh token mechanism can be implemented for longer sessions
 * 
 * Security Best Practices:
 * - Token transmitted over HTTPS only
 * - Token stored securely on client (HttpOnly cookie or secure storage)
 * - Token includes expiration time
 * - Token can be revoked server-side if needed
 * 
 * Client Usage Example:
 * <pre>
 * // JavaScript/React
 * const response = await fetch('/api/auth/login', {
 *   method: 'POST',
 *   headers: { 'Content-Type': 'application/json' },
 *   body: JSON.stringify({ userName: 'admin', password: 'admin123' })
 * });
 * 
 * const data = await response.json();
 * if (data.success) {
 *   localStorage.setItem('token', data.token);
 *   localStorage.setItem('user', JSON.stringify(data.user));
 *   // Redirect to dashboard
 * } else {
 *   alert(data.message);
 * }
 * </pre>
 * 
 * @see LoginRequest
 * @see UserDto
 * @see com.flogin.service.JwtService
 */
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserDto user;

    // Constructor đầy đủ cho trường hợp thành công
    public LoginResponse(boolean success, String message, String token, UserDto user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    // Constructor với token nhưng không có user (backward compatibility)
    public LoginResponse(boolean success, String message, String token) {
        this(success, message, token, null);
    }

    // Constructor đơn giản cho các trường hợp lỗi
    public LoginResponse(boolean success, String message) {
        this(success, message, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDto getUser() { return user; }
}
