package com.team4.wildlifetracker.dto;

import java.time.LocalDateTime;

/**
 * DTO for Report entity responses.
 * Used to transfer report data to clients without exposing internal entity structure.
 */
public class ReportResponse {

    private Long id;
    private Long sightingId;
    private Long userId;
    private String username;
    private String reason;
    private LocalDateTime timestamp;

    public ReportResponse() {
    }

    public ReportResponse(Long id, Long sightingId, Long userId, String username, 
                         String reason, LocalDateTime timestamp) {
        this.id = id;
        this.sightingId = sightingId;
        this.userId = userId;
        this.username = username;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
