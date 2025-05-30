package com.CESIZen.prod.dto.diagnostic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DiagnosticScoreRangeDTO {
    private Long id;
    @Min(value = 0, message = "Le minimum de points doit être supérieur ou égal à 0")
    private int minPoints;
    @Min(value = 0, message = "Le maximum de points doit être supérieur ou égal à 0")
    private int maxPoints;
    @NotBlank(message = "Le message ne peut pas être vide")
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
