package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreateCommentRequest;
import com.devdan.minisosmed.model.response.CommentResponse;
import com.devdan.minisosmed.model.response.PagingResponse;
import com.devdan.minisosmed.model.response.PostResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/api/posts/{postId}/comments")
    public WebResponse<List<CommentResponse>> getAllCommentByPostId(
            @PathVariable("postId") String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponse> responsePage = commentService.getAllCommentByPostId(postId, page, size);

        return WebResponse.<List<CommentResponse>>builder()
                .data(responsePage.getContent())
                .pagingResponse(PagingResponse.builder()
                        .currentPage(responsePage.getNumber())
                        .totalPage(responsePage.getTotalPages())
                        .size(responsePage.getSize())
                        .build())
                .build();
    }

}
