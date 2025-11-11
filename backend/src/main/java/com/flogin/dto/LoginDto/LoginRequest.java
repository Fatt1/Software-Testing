package com.flogin.dto.LoginDto;

import jakarta.validation.constraints.*;

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
