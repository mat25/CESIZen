package com.CESIZen.prod.dto.diagnostic;

public class DiagnosticResultDTO {
    private int score;
    private String level;

    public DiagnosticResultDTO(int score, String level) {
        this.score = score;
        this.level = level;
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
}
