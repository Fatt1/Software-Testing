package com.flogin.repository.interfaces;

import com.flogin.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUserName(String userName);
}
