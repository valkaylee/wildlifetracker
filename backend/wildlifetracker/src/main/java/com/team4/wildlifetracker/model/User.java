package com.team4.wildlifetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Additional user fields can be added here later (e.g., email, profile info)
    @Column(length = 100)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Column(length = 500)
    private String profilePictureUrl;
    
    @Column(nullable = false)
    private Integer totalAnimalsLogged = 0;

    @Column(nullable = false)
    private Integer uniqueSpeciesCount = 0;

    @Column
    private LocalDateTime lastActivityDate;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.totalAnimalsLogged = 0;
        this.uniqueSpeciesCount = 0;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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
}
