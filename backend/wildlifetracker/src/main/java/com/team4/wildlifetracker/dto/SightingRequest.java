package com.team4.wildlifetracker.dto;

/**
 * DTO for creating/updating sightings.
 * Separates input data from entity structure.
 */
public class SightingRequest {
    private String species;
    private String location;
    private String description;
    private String imageUrl;
    private Integer pixelX;
    private Integer pixelY;
    private Long userId;

    public SightingRequest() {}

    public SightingRequest(String species, String location, String description, 
                          String imageUrl, Long userId) {
        this.species = species;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    // Getters and setters
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
