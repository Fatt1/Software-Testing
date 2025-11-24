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
 * AuthService - Service xử lý authentication
 * Sử dụng Bean Validation (Jakarta Validation) để validate LoginRequest
 */
@Service
public class AuthService {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private Validator validator;

    // Constructor cho testing (để inject mock objects)
    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    // Constructor mặc định cho Spring
    public AuthService() {
    }

    /**
     * Authenticate user với username và password
     * Sử dụng Bean Validation để validate input
     */
    public LoginResponse authenticate(LoginRequest request) {
        // Validate request bằng Bean Validation annotations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            // Lấy lỗi đầu tiên
            String errorMessage = violations.iterator().next().getMessage();
            return new LoginResponse(false, errorMessage);
        }

        // Kiểm tra user có tồn tại không
        Optional<User> userOptional = userRepository.findByUserNameIgnoreCase(request.getUserName());
        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Login thất bại với user name không tồn tại");
        }
        
        User user = userOptional.get();
        
        // Verify password bằng BCryptPasswordEncoder
        boolean matchPassword = passwordEncoder.matches(request.getPassword(), user.getHashPassword());

        if (!matchPassword) {
            return new LoginResponse(false, "Login với password sai");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Tạo UserDto từ User entity
        UserDto userDto = new UserDto(user.getUserName(), user.getEmail());

        return new LoginResponse(true, "Login thành công", token, userDto);
    }

    /**
     * Validate LoginRequest và trả về list of error messages
     */
    public java.util.List<String> validate(LoginRequest request) {
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }
}
