package com.flogin.repository.interfaces;

import com.flogin.entity.User;

/**
 * @see User
 * @see com.flogin.service.AuthService
 * @see com.flogin.dto.LoginDto.LoginResponse
 */
public interface JwtService {
    String generateToken(User user);
}
