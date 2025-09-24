package com.devdan.minisosmed.service;

import com.devdan.minisosmed.entity.Like;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.repository.LikeRepository;
import com.devdan.minisosmed.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class LikeService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Transactional
    public void likePost(User user, String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (likeRepository.existsByUserAndPost(user, post)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already liked");
        }

        Like like = new Like();
        like.setId(UUID.randomUUID().toString());
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(User user, String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!likeRepository.existsByUserAndPost(user, post)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found");
        }

        likeRepository.deleteByUserAndPost(user, post);
    }

    public long countLikes(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return likeRepository.countByPost(post);
    }
}
