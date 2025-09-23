package com.devdan.minisosmed.model.request;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostRequest {
    @Size(max = 255)
    private String body;

    @Transient
    private String imageUrl;

    @Pattern(regexp = "PUBLISHED|DRAFT", message = "Status hanya boleh PUBLISHED atau DRAFT")
    private String status;

    public void setStatus(String status) {
        this.status = (status == null) ? null : status.toUpperCase();
    }
}
