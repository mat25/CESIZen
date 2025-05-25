package com.CESIZen.prod.dto.resource;

import com.CESIZen.prod.entity.Resource;

public class ResourceDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;

    public static ResourceDTO fromEntity(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setTitle(resource.getTitle());
        dto.setContent(resource.getContent());
        dto.setImageUrl(resource.getImageUrl());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

