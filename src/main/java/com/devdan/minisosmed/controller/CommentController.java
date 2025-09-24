package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreateCommentRequest;
import com.devdan.minisosmed.model.response.CommentResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping(
            path = "/api/posts/{postId}/comments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CommentResponse> create(
            User user,
            @PathVariable("postId") String postId,
            @RequestBody CreateCommentRequest request
            ) {
        request.setPostId(postId);
        CommentResponse response = commentService.create(user, request);
        return WebResponse.<CommentResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/posts/{postId}/comments/{commentId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public  WebResponse<String> delete(
            User user,
            @PathVariable("commentId") String commentId
    ){
        commentService.delete(user, commentId);
        return WebResponse.<String>builder().data("OK").build();
    }

}
