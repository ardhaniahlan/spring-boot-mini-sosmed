package com.devdan.minisosmed.service;

import com.devdan.minisosmed.entity.Comment;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreateCommentRequest;
import com.devdan.minisosmed.model.response.CommentResponse;
import com.devdan.minisosmed.repository.CommentRepository;
import com.devdan.minisosmed.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public CommentResponse create(User user, CreateCommentRequest request){
        validationService.validate(request);

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody(request.getBody());
        comment.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);

        return toCommentResponse(comment);
    }

    @Transactional
    public void delete(User user, String commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public Page<CommentResponse> getAllCommentByPostId(String postId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findAllByPost_Id(postId, pageable);
        return commentPage.map(this::toCommentResponse);
    }

    private CommentResponse toCommentResponse(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
