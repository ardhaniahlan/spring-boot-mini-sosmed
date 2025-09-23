package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreatePostRequest;
import com.devdan.minisosmed.model.request.UpdatePostRequest;
import com.devdan.minisosmed.model.response.PagingResponse;
import com.devdan.minisosmed.model.response.PostResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.PostService;
import com.devdan.minisosmed.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private StorageService storageService;

    @PostMapping(
            path = "/api/posts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public WebResponse<PostResponse> create(
            User user,
            @ModelAttribute CreatePostRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
            ) throws IOException {

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = storageService.save(imageFile);
        }

        request.setImageUrl(imageUrl);
        PostResponse response = postService.create(user, request);
        return WebResponse.<PostResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/posts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<PostResponse>> getAllPostPublished(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        Page<PostResponse> responsePage = postService.getAllPostPublished(page, size);

        return WebResponse.<List<PostResponse>>builder()
                .data(responsePage.getContent())
                .pagingResponse(PagingResponse.builder()
                        .currentPage(responsePage.getNumber())
                        .totalPage(responsePage.getTotalPages())
                        .size(responsePage.getSize())
                        .build())
                .build();
    }


    @GetMapping("/api/users/{userId}/posts")
    public WebResponse<List<PostResponse>> getAllPostByUserId(
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostResponse> responsePage = postService.getAllPostPublishedByUserId(userId, page, size);

        return WebResponse.<List<PostResponse>>builder()
                .data(responsePage.getContent())
                .pagingResponse(PagingResponse.builder()
                        .currentPage(responsePage.getNumber())
                        .totalPage(responsePage.getTotalPages())
                        .size(responsePage.getSize())
                        .build())
                .build();
    }

    @GetMapping("/api/users/me/posts")
    public WebResponse<List<PostResponse>> getAllMyPosts(
            User user,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostResponse> responsePage = postService.getAllPostByUser(user, status, page, size);

        return WebResponse.<List<PostResponse>>builder()
                .data(responsePage.getContent())
                .pagingResponse(PagingResponse.builder()
                        .currentPage(responsePage.getNumber())
                        .totalPage(responsePage.getTotalPages())
                        .size(responsePage.getSize())
                        .build())
                .build();
    }

    @DeleteMapping(
            path = "/api/posts/{postId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("postId") String postId){
        postService.delete(user, postId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PatchMapping(
            path = "/api/posts/{postId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<PostResponse> update(
            User user,
            @RequestBody UpdatePostRequest request,
            @PathVariable("postId") String postId
    ){
        request.setId(postId);
        PostResponse response = postService.update(user, request);
        return WebResponse.<PostResponse>builder().data(response).build();
    }
}
