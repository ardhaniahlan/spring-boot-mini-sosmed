package com.devdan.minisosmed.model.response;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private String id;

    private String body;

    private String imageUrl;

    private String status;

    private String username;

    private String createdAt;
}
