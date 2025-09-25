package com.devdan.minisosmed.repository;

import com.devdan.minisosmed.entity.Comment;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment> findAllByPost_Id(String postId, Pageable pageable);

}
