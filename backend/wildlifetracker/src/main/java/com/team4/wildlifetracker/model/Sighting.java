package com.team4.wildlifetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sightings")
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String species;

    private String location;

    private String description;

    private String imageUrl;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Pixel coordinates on the campus map image
    @Column(name = "pixel_x")
    private Integer pixelX;
    
    @Column(name = "pixel_y")
    private Integer pixelY;

    // Link to the user who created this sighting
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Sighting() {}

    public Sighting(String species, String location, String description, String imageUrl, User user) {
        this.species = species;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public Sighting(String species, String location, String description, String imageUrl, Integer pixelX, Integer pixelY, User user) {
        this.species = species;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.user = user;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getPixelX() { return pixelX; }
    public void setPixelX(Integer pixelX) { this.pixelX = pixelX; }

    public Integer getPixelY() { return pixelY; }
    public void setPixelY(Integer pixelY) { this.pixelY = pixelY; }
}
