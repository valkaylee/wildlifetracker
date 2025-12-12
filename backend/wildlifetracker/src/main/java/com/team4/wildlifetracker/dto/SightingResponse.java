package com.team4.wildlifetracker.dto;

import java.time.LocalDateTime;

/**
 * DTO for Sighting responses.
 * Includes basic user info without exposing full user entity.
 */
public class SightingResponse {
    private Long id;
    private String species;
    private String location;
    private String description;
    private String imageUrl;
    private LocalDateTime timestamp;
    private Integer pixelX;
    private Integer pixelY;
    private Long userId;
    private String username;
    private String displayName;

    public SightingResponse() {}

    public SightingResponse(Long id, String species, String location, String description,
                           String imageUrl, LocalDateTime timestamp, Integer pixelX, Integer pixelY,
                           Long userId, String username, String displayName) {
        this.id = id;
        this.species = species;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public Integer getPixelX() {
        return pixelX;
    }

    public void setPixelX(Integer pixelX) {
        this.pixelX = pixelX;
    }

    public Integer getPixelY() {
        return pixelY;
    }

    public void setPixelY(Integer pixelY) {
        this.pixelY = pixelY;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
