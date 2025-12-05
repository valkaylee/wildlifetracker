package com.team4.wildlifetracker.dto;

import java.time.LocalDateTime;

/**
 * DTO for Notification responses.
 */
public class NotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;
    private Long userId;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String message, LocalDateTime timestamp, 
                               boolean isRead, Long userId) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.userId = userId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
