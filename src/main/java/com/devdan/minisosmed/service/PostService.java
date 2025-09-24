package com.devdan.minisosmed.service;

import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreatePostRequest;
import com.devdan.minisosmed.model.request.UpdatePostRequest;
import com.devdan.minisosmed.model.response.PostResponse;
import com.devdan.minisosmed.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public PostResponse create(User user, CreatePostRequest request){
        validationService.validate(request);

        if ("PUBLISHED".equalsIgnoreCase(request.getStatus())) {
            boolean bodyKosong = (request.getBody() == null || request.getBody().isBlank());
            boolean imageKosong = (request.getImageUrl() == null || request.getImageUrl().isBlank());

            if (bodyKosong && imageKosong) {
                throw new IllegalArgumentException(
                        "Post tidak boleh kosong jika status PUBLISHED. Minimal body atau image harus ada."
                );
            }
        }

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody(request.getBody());
        post.setImageUrl(request.getImageUrl());
        post.setStatus(request.getStatus().toUpperCase());
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);

        postRepository.save(post);
        return toPostResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPostPublished(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAllByStatus("PUBLISHED", pageable);

        return postPage.map(this::toPostResponse);
    }


    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPostPublishedByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAllByUser_IdAndStatus(userId, "PUBLISHED", pageable);

        return postPage.map(this::toPostResponse);
    }


    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPostByUser(User user, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;
        if (status != null && !status.isBlank()) {
            postPage = postRepository.findAllByUserAndStatus(user, status.toUpperCase(), pageable);
        } else {
            postPage = postRepository.findAllByUser(user, pageable);
        }

        return postPage.map(this::toPostResponse);
    }

    @Transactional
    public void delete(User user, String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your post");
        }

        postRepository.delete(post);
    }

    @Transactional
    public PostResponse update(User user, UpdatePostRequest request){
        validationService.validate(request);
        Post post = postRepository.findByIdAndUser(request.getId(), user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        post.setBody(request.getBody());
        postRepository.save(post);

        return toPostResponse(post);
    }

    public PostResponse toPostResponse(Post post){
        return PostResponse.builder()
                .id(post.getId())
                .body(post.getBody())
                .imageUrl(post.getImageUrl())
                .status(post.getStatus())
                .username(post.getUser().getUsername())
                .createdAt(String.valueOf(post.getCreatedAt()))
                .build();
    }

}
