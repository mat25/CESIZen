package com.CESIZen.prod.dto.user;

import com.CESIZen.prod.dto.diagnostic.DiagnosticExportDTO;
import com.CESIZen.prod.entity.User;

import java.util.List;

public class UserExportDTO {
    private String username;
    private String email;
    private String role;
    private List<DiagnosticExportDTO> diagnostics;

    public UserExportDTO() {}

    public UserExportDTO(User user, List<DiagnosticExportDTO> diagnostics) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().getName().name();
        this.diagnostics = diagnostics;
    }

    // Getters et setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<DiagnosticExportDTO> getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(List<DiagnosticExportDTO> diagnostics) {
        this.diagnostics = diagnostics;
    }
}
