package com.CESIZen.prod.dto.diagnostic;

import com.CESIZen.prod.entity.DiagnosticResult;

import java.time.LocalDateTime;
import java.util.List;

public class DiagnosticHistoryDTO {
    private int score;
    private String level;
    private LocalDateTime submittedAt;
    private List<DiagnosticResultEventDTO> events;

    public DiagnosticHistoryDTO(DiagnosticResult result, String levelMessage) {
        this.score = result.getScore();
        this.level = levelMessage;
        this.submittedAt = result.getSubmittedAt();
        this.events = result.getEventDetails().stream()
                .map(DiagnosticResultEventDTO::new)
                .toList();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<DiagnosticResultEventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<DiagnosticResultEventDTO> events) {
        this.events = events;
    }
}

