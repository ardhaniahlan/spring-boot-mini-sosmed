package com.devdan.minisosmed.repository;

import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post> {
    Page<Post> findAllByStatus(String status, Pageable pageable);
    Page<Post> findAllByUser_IdAndStatus(String userId, String status, Pageable pageable);
    Page<Post> findAllByUser(User user, Pageable pageable);
    Page<Post> findAllByUserAndStatus(User user, String status, Pageable pageable);
    Optional<Post> findByIdAndUser(String id, User user);
}
