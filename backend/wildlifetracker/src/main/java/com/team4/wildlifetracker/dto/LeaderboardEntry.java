package com.team4.wildlifetracker.dto;

import java.time.LocalDateTime;

public class LeaderboardEntry {
    private Long userId;
    private String username;
    private String displayName;
    private String profilePictureUrl;
    private Integer totalAnimalsLogged;
    private Integer uniqueSpeciesCount;
    private LocalDateTime lastActivityDate;
    private Integer rank;

    public LeaderboardEntry() {}

    public LeaderboardEntry(Long userId, String username, String displayName, 
                           String profilePictureUrl, Integer totalAnimalsLogged, 
                           Integer uniqueSpeciesCount, LocalDateTime lastActivityDate) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.totalAnimalsLogged = totalAnimalsLogged;
        this.uniqueSpeciesCount = uniqueSpeciesCount;
        this.lastActivityDate = lastActivityDate;
    }

    // Getters and setters
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Integer getTotalAnimalsLogged() {
        return totalAnimalsLogged;
    }

    public void setTotalAnimalsLogged(Integer totalAnimalsLogged) {
        this.totalAnimalsLogged = totalAnimalsLogged;
    }

    public Integer getUniqueSpeciesCount() {
        return uniqueSpeciesCount;
    }

    public void setUniqueSpeciesCount(Integer uniqueSpeciesCount) {
        this.uniqueSpeciesCount = uniqueSpeciesCount;
    }

    public LocalDateTime getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDateTime lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}