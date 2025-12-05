package com.team4.wildlifetracker.dto;

/**
 * DTO for Profile entity responses.
 * Used to transfer profile data to clients without exposing internal entity structure.
 */
public class ProfileResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long animalsLogged;
    private Long speciesSeen;
    private Long assists;
    private String mostFrequentArea;

    public ProfileResponse() {
    }

    public ProfileResponse(Long id, Long userId, String username, Long animalsLogged, 
                          Long speciesSeen, Long assists, String mostFrequentArea) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.animalsLogged = animalsLogged;
        this.speciesSeen = speciesSeen;
        this.assists = assists;
        this.mostFrequentArea = mostFrequentArea;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getAnimalsLogged() {
        return animalsLogged;
    }

    public void setAnimalsLogged(Long animalsLogged) {
        this.animalsLogged = animalsLogged;
    }

    public Long getSpeciesSeen() {
        return speciesSeen;
    }

    public void setSpeciesSeen(Long speciesSeen) {
        this.speciesSeen = speciesSeen;
    }

    public Long getAssists() {
        return assists;
    }

    public void setAssists(Long assists) {
        this.assists = assists;
    }

    public String getMostFrequentArea() {
        return mostFrequentArea;
    }

    public void setMostFrequentArea(String mostFrequentArea) {
        this.mostFrequentArea = mostFrequentArea;
    }
}
