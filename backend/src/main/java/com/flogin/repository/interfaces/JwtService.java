package com.flogin.repository.interfaces;

import com.flogin.entity.User;

public interface JwtService {
    String generateToken(User user);
}
