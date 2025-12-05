package com.team4.wildlifetracker.model;

import jakarta.persistence.*;

/**
 * Profile entity representing user statistics and achievements.
 * Maps to the profiles table in the database.
 */
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "animals_logged", nullable = false)
    private Long animalsLogged = 0L;

    @Column(name = "species_seen", nullable = false)
    private Long speciesSeen = 0L;

    @Column(nullable = false)
    private Long assists = 0L;

    @Column(name = "most_frequent_area", length = 255)
    private String mostFrequentArea;

    public Profile() {
    }

    public Profile(User user) {
        this.user = user;
        this.animalsLogged = 0L;
        this.speciesSeen = 0L;
        this.assists = 0L;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    /**
     * Increment the animals logged counter.
     */
    public void incrementAnimalsLogged() {
        this.animalsLogged++;
    }

    /**
     * Increment the assists counter.
     */
    public void incrementAssists() {
        this.assists++;
    }
}
