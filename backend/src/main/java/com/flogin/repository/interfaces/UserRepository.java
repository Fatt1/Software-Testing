package com.flogin.repository.interfaces;

import com.flogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Tìm user theo username
     * Spring Data JPA tự động implement method này dựa trên naming convention
     * @param userName username cần tìm
     * @return Optional<User>
     */
    Optional<User> findByUserName(String userName);
}
