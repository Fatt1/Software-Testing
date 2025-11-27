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

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }


    public AuthService() {
    }

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
