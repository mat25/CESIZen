package com.CESIZen.prod.dto.diagnostic;

import java.util.List;

public class DiagnosticSubmitDTO {
    private List<Long> selectedEventIds;

    public List<Long> getSelectedEventIds() {
        return selectedEventIds;
    }

    public void setSelectedEventIds(List<Long> selectedEventIds) {
        this.selectedEventIds = selectedEventIds;
    }
}
