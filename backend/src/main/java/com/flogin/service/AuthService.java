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
     */
    public AuthService() {
    }

    /**
     * Core authentication method - xác thực user credentials
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
     * Input: { userName: "admin", password: "admin123" }
     * → Validate: OK
     * → Find User: Found
     * → Password Match: OK
     * → Generate Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * Output: { success: true, message: "Login thành công", token: "...", user: {...} }
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


}
