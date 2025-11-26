package com.flogin.service;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.LoginDto.LoginResponse;
import com.flogin.dto.UserDtos.UserDto;
import com.flogin.entity.User;
import com.flogin.repository.interfaces.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AuthService - Service Layer for Authentication Business Logic
 * 
 * <p>Service này xử lý toàn bộ business logic liên quan đến authentication,
 * bao gồm validation, password verification, và JWT token generation.</p>
 * 
 * <p>Core Responsibilities:</p>
 * <ul>
 *   <li>Validate login credentials sử dụng Bean Validation (Jakarta Validation)</li>
 *   <li>Verify user existence trong database</li>
 *   <li>Check password correctness với BCrypt hashing</li>
 *   <li>Generate JWT tokens cho authenticated users</li>
 *   <li>Return detailed error messages cho failed authentication attempts</li>
 * </ul>
 * 
 * <p>Security Features:</p>
 * <ul>
 *   <li>BCryptPasswordEncoder cho password hashing (không lưu plain text passwords)</li>
 *   <li>JWT tokens với expiration time</li>
 *   <li>Input validation để prevent injection attacks</li>
 *   <li>Detailed error messages để support debugging</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>JwtService - Để generate và validate JWT tokens</li>
 *   <li>UserRepository - Để access user data từ database</li>
 *   <li>PasswordEncoder - BCrypt implementation cho password hashing</li>
 *   <li>Validator - Jakarta Bean Validation cho input validation</li>
 * </ul>
 * 
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 * @see JwtService
 * @see UserRepository
 */
@Service
public class AuthService {
    
    /**
     * JwtService instance để generate và manage JWT tokens
     */
    @Autowired
    private JwtService jwtService;
    
    /**
     * UserRepository để access user data từ database
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * PasswordEncoder (BCrypt) để hash và verify passwords securely
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Jakarta Bean Validator để validate request objects
     */
    @Autowired
    private Validator validator;

    /**
     * Constructor cho testing purposes
     * 
     * <p>Constructor này cho phép inject mock objects khi viết unit tests,
     * giúp test service logic mà không cần real dependencies.</p>
     * 
     * @param jwtService Mock JwtService cho testing
     * @param userRepository Mock UserRepository cho testing
     * @param passwordEncoder Mock PasswordEncoder cho testing
     * @param validator Mock Validator cho testing
     */
    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    /**
     * Default constructor cho Spring dependency injection
     * 
     * <p>Spring sử dụng constructor này để create bean và inject dependencies
     * thông qua @Autowired annotations.</p>
     */
    public AuthService() {
    }

    /**
     * Core authentication method - xác thực user credentials
     * 
     * <p>Method này thực hiện full authentication flow:</p>
     * <ol>
     *   <li>Validate input data (username, password format) sử dụng Bean Validation</li>
     *   <li>Kiểm tra user existence trong database</li>
     *   <li>Verify password với BCrypt hashing algorithm</li>
     *   <li>Generate JWT token nếu authentication thành công</li>
     *   <li>Return response với appropriate success/error message</li>
     * </ol>
     * 
     * <p><b>Authentication Process:</b></p>
     * <pre>
     * 1. Input Validation
     *    - Check required fields (username, password)
     *    - Validate format và length constraints
     *    - Return error nếu validation fails
     * 
     * 2. User Verification
     *    - Query database cho user với given username
     *    - Return error nếu user không tồn tại
     * 
     * 3. Password Check
     *    - Compare provided password với hashed password trong DB
     *    - Sử dụng BCrypt's secure comparison
     *    - Return error nếu password không match
     * 
     * 4. Token Generation
     *    - Create JWT token với user information
     *    - Set expiration time
     *    - Return token với success response
     * </pre>
     * 
     * <p><b>Example Success Flow:</b></p>
     * <pre>
     * Input: { userName: "admin", password: "admin123" }
     * → Validate: OK
     * → Find User: Found
     * → Password Match: OK
     * → Generate Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * Output: { success: true, message: "Login thành công", token: "...", user: {...} }
     * </pre>
     * 
     * <p><b>Possible Error Scenarios:</b></p>
     * <ul>
     *   <li>Validation Error: "Username is required", "Password must be at least 6 characters"</li>
     *   <li>User Not Found: "Login thất bại với user name không tồn tại"</li>
     *   <li>Wrong Password: "Login với password sai"</li>
     * </ul>
     * 
     * @param request LoginRequest object chứa userName và password cần authenticate
     * @return LoginResponse object chứa:
     *         - success: boolean indicating authentication result
     *         - message: descriptive message (success or error)
     *         - token: JWT token nếu thành công, null nếu thất bại
     *         - user: UserDto object nếu thành công, null nếu thất bại
     */
    public LoginResponse authenticate(LoginRequest request) {
        // Step 1: Validate request bằng Bean Validation annotations
        // Kiểm tra @NotBlank, @Size, và các constraints khác
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            // Lấy lỗi đầu tiên để trả về cho client
            String errorMessage = violations.iterator().next().getMessage();
            return new LoginResponse(false, errorMessage);
        }

        // Step 2: Kiểm tra user có tồn tại trong database không
        Optional<User> userOptional = userRepository.findByUserName(request.getUserName());
        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Login thất bại với user name không tồn tại");
        }
        
        User user = userOptional.get();
        
        // Step 3: Verify password bằng BCryptPasswordEncoder
        // So sánh plain text password với hashed password trong database
        boolean matchPassword = passwordEncoder.matches(request.getPassword(), user.getHashPassword());

        if (!matchPassword) {
            return new LoginResponse(false, "Login với password sai");
        }
        
        // Step 4: Generate JWT token cho authenticated user
        String token = jwtService.generateToken(user);
        
        // Tạo UserDto từ User entity để trả về (không expose sensitive data)
        UserDto userDto = new UserDto(user.getUserName(), user.getEmail());

        // Return success response với token và user info
        return new LoginResponse(true, "Login thành công", token, userDto);
    }

    /**
     * Validate LoginRequest và trả về list of error messages
     * 
     * <p>Helper method để validate LoginRequest object và collect
     * tất cả validation errors (không chỉ error đầu tiên).</p>
     * 
     * <p>Sử dụng cho:</p>
     * <ul>
     *   <li>Testing purposes - kiểm tra tất cả validation rules</li>
     *   <li>Detailed error reporting - show all errors to user</li>
     *   <li>Form validation - hiển thị multiple field errors</li>
     * </ul>
     * 
     * <p><b>Example Usage:</b></p>
     * <pre>
     * LoginRequest request = new LoginRequest("", "");
     * List&lt;String&gt; errors = authService.validate(request);
     * // errors = ["Username is required", "Password is required"]
     * </pre>
     * 
     * @param request LoginRequest object cần validate
     * @return List of error messages (empty list nếu không có errors)
     */
    public java.util.List<String> validate(LoginRequest request) {
        // Validate request và collect tất cả constraint violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Convert violations to list of error messages
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }
}
