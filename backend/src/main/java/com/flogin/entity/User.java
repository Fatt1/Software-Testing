package com.flogin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String userName;
    
    @Column(name = "hash_password", nullable = false, length = 255)
    private String hashPassword; // mật khẩu đã được mã hóa

    // Constructor mặc định (bắt buộc cho JPA)
    public User() {
    }

    public User(Long id, String userName, String hashPassword) {
        this.hashPassword = hashPassword;
        this.id = id;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
}
