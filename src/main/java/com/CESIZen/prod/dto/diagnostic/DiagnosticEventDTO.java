package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DiagnosticEventDTO {
    @NotNull(message = "L'id ne peut pas être nul")
    private Long id;
    @NotBlank(message = "Le label ne peut pas être vide")
    private String label;
    @Min(value = 0, message = "Les points doivent être positifs ou nuls")
    private int points;

    public DiagnosticEventDTO() {}

    public DiagnosticEventDTO(DiagnosticEvent event) {
        this.setId(event.getId());
        this.setLabel(event.getLabel());
        this.setPoints(event.getPoints());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
