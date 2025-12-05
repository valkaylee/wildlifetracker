package com.team4.wildlifetracker.dto;

import com.team4.wildlifetracker.model.User;
import java.time.LocalDateTime;

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
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public Integer getTotalAnimalsLogged() { return totalAnimalsLogged; }
    public Integer getUniqueSpeciesCount() { return uniqueSpeciesCount; }
    public LocalDateTime getLastActivityDate() { return lastActivityDate; }
}