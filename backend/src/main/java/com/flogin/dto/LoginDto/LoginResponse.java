package com.flogin.dto.LoginDto;

import com.flogin.dto.UserDtos.UserDto;


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
