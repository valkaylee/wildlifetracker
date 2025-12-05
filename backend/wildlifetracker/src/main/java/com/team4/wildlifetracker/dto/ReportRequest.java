package com.team4.wildlifetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating report requests.
 * Contains validation annotations to ensure data integrity.
 */
public class ReportRequest {

    @NotNull(message = "Sighting ID is required")
    private Long sightingId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Reason is required")
    private String reason;

    public ReportRequest() {
    }

    public ReportRequest(Long sightingId, Long userId, String reason) {
        this.sightingId = sightingId;
        this.userId = userId;
        this.reason = reason;
    }

    // Getters and setters
    public Long getSightingId() {
        return sightingId;
    }

    public void setSightingId(Long sightingId) {
        this.sightingId = sightingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
