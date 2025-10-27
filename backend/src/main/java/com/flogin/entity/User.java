package com.flogin.entity;

public class User {
    private Long id;
    private String userName;
    private String hashPassword; // mật khẩu nay đã được mã hóa

    public User( Long id,  String userName, String hashPassword){
        this.hashPassword = hashPassword;
        this.id = id;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getHashPassword() {
        return hashPassword;
    }
}
