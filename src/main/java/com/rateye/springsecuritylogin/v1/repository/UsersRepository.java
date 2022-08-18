package com.rateye.springsecuritylogin.v1.repository;

import com.rateye.springsecuritylogin.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(String id);
    boolean existsById(String id);
    boolean existsByEmail(String email);
}