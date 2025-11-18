package com.flogin.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để hash password cho testing
 * Chạy main method này để generate BCrypt hash của password
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash một số password mẫu
        String[] passwords = {"Test123", "Pass456", "Admin999"};
        
        System.out.println("=== BCrypt Password Hashes ===\n");
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
        }
        
        // Verify hash works
        System.out.println("=== Verification Test ===");
        String testPassword = "Test123";
        String testHash = encoder.encode(testPassword);
        boolean matches = encoder.matches(testPassword, testHash);
        System.out.println("Password '" + testPassword + "' matches hash: " + matches);
    }
}
