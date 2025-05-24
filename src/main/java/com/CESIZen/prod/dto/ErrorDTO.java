package com.CESIZen.prod.dto;

public class ErrorDTO {
    private String message;

    public ErrorDTO(String message) {
        this.message = message;
    }

    // Getters et setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
