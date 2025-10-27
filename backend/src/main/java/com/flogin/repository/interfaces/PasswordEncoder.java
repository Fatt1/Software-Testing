package com.flogin.repository.interfaces;

public interface PasswordEncoder {
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
