package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping(
            path = "/api/posts/{postId}/likes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> like(User user, @PathVariable("postId") String postId) {
        likeService.likePost(user, postId);
        return WebResponse.<String>builder().data("Liked").build();
    }

    @DeleteMapping(
            path = "/api/posts/{postId}/likes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> unlike(User user, @PathVariable("postId") String postId) {
        likeService.unlikePost(user, postId);
        return WebResponse.<String>builder().data("Unliked").build();
    }

    @GetMapping(
            path = "/api/posts/{postId}/likes/count",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Long> countLikes(@PathVariable("postId") String postId) {
        long count = likeService.countLikes(postId);
        return WebResponse.<Long>builder().data(count).build();
    }
}
