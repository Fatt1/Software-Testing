package com.flogin.dto.LoginDto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;

    // Constructors, Getters
    public LoginResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    // Constructor đơn giản cho các trường hợp lỗi
    public LoginResponse(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
}
