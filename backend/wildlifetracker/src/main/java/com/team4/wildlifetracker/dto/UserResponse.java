package com.team4.wildlifetracker.dto;

import java.time.LocalDateTime;

import com.team4.wildlifetracker.model.User;

public class UserResponse {

    private Long id;
    private String username;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private Integer totalAnimalsLogged;
    private Integer uniqueSpeciesCount;
    private LocalDateTime lastActivityDate;

    public UserResponse() {}

    public UserResponse(Long id,
                        String username,
                        String displayName,
                        String bio,
                        String profilePictureUrl,
                        Integer totalAnimalsLogged,
                        Integer uniqueSpeciesCount,
                        LocalDateTime lastActivityDate) {

        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.totalAnimalsLogged = totalAnimalsLogged;
        this.uniqueSpeciesCount = uniqueSpeciesCount;
        this.lastActivityDate = lastActivityDate;
    }

    public static UserResponse fromEntity(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getDisplayName(),
                u.getBio(),
                u.getProfilePictureUrl(),
                u.getTotalAnimalsLogged(),
                u.getUniqueSpeciesCount(),
                u.getLastActivityDate()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    public Integer getTotalAnimalsLogged() { return totalAnimalsLogged; }
    public void setTotalAnimalsLogged(Integer totalAnimalsLogged) { this.totalAnimalsLogged = totalAnimalsLogged; }
    
    public Integer getUniqueSpeciesCount() { return uniqueSpeciesCount; }
    public void setUniqueSpeciesCount(Integer uniqueSpeciesCount) { this.uniqueSpeciesCount = uniqueSpeciesCount; }
    
    public LocalDateTime getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDateTime lastActivityDate) { this.lastActivityDate = lastActivityDate; }
}
