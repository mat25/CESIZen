package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticEvent;

public class DiagnosticEventDTO {
    private Long id;
    private String label;
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
