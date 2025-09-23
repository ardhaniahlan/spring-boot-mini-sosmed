package com.devdan.minisosmed.repository;

import com.devdan.minisosmed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Cek apakah username sudah ada
    boolean existsByUsername(String username);

    // Cek apakah email sudah ada
    boolean existsByEmail(String email);
}
