package com.flogin.repository.interfaces;

/**
 * @see User
 * @see com.flogin.service.AuthService
 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
public interface PasswordEncoder {
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
