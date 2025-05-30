package com.CESIZen.prod.dto.resource;

import jakarta.validation.constraints.NotBlank;

public class CreateResourceDTO {
    @NotBlank(message = "Le titre ne peut pas être vide")
    private String title;
    @NotBlank(message = "Le contenu ne peut pas être vide")
    private String content;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

