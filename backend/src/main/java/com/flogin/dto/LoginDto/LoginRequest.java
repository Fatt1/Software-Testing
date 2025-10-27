package com.flogin.dto.LoginDto;

public class LoginRequest {
    private String userName;
    private  String password;

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
