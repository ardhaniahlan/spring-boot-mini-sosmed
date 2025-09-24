package com.devdan.minisosmed.repository;

import com.devdan.minisosmed.entity.Like;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    long countByPost(Post post);
}
