package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticResultEvent;

public class DiagnosticResultEventDTO {
    private String label;
    private int points;
    private int occurrences;

    public DiagnosticResultEventDTO(DiagnosticResultEvent entity) {
        this.label = entity.getEvent().getLabel();
        this.points = entity.getEvent().getPoints();
        this.occurrences = entity.getOccurrences();
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

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
}

