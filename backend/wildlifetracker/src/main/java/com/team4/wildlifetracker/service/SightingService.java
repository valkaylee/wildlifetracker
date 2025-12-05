package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.SightingRequest;
import com.team4.wildlifetracker.dto.SightingResponse;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.SightingRepository;
import com.team4.wildlifetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;
    private final UserRepository userRepository;

    public SightingService(SightingRepository sightingRepository, UserRepository userRepository) {
        this.sightingRepository = sightingRepository;
        this.userRepository = userRepository;
    }

    // CREATE from DTO
    @Transactional
    public SightingResponse createSighting(SightingRequest request) {
        // Get user - create default user if not found
        Long userId = request.getUserId();
        if (userId == null) {
            userId = 1L; // Default to user ID 1
        }
        
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    // Create a default anonymous user if the requested user doesn't exist
                    User defaultUser = userRepository.findByUsername("anonymous")
                            .orElseGet(() -> {
                                User newUser = new User("anonymous", "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
                                newUser.setDisplayName("Anonymous User");
                                return userRepository.save(newUser);
                            });
                    return defaultUser;
                });
        
        // Create entity
        Sighting sighting = new Sighting(
            request.getSpecies(),
            request.getLocation(),
            request.getDescription(),
            request.getImageUrl(),
            request.getPixelX(),
            request.getPixelY(),
            user
        );
        
    	// Save the sighting
        Sighting saved = sightingRepository.save(sighting);
        
        // Update user statistics
        updateUserStatistics(user.getId());
        
        return toSightingResponse(saved);
    }
    
    // CREATE from entity (for backward compatibility)
    @Transactional
    public Sighting createSighting(Sighting sighting) {
    	// Save the sighting
        Sighting saved = sightingRepository.save(sighting);
        
        // Update user statistics
        if (sighting.getUser() != null) {
            updateUserStatistics(sighting.getUser().getId());
        }
        
        return saved;
    }

    // READ (single)
    public Sighting findById(Long id) {
        return sightingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sighting not found"));
    }
    
    // READ (single) as DTO
    public SightingResponse findByIdAsDto(Long id) {
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sighting not found"));
        return toSightingResponse(sighting);
    }

    // READ (all)
    public List<Sighting> findAll() {
        return sightingRepository.findAll();
    }
    
    // READ (all) as DTOs
    public List<SightingResponse> findAllAsDto() {
        return sightingRepository.findAll().stream()
                .map(this::toSightingResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public Sighting update(Long id, Sighting updated) {
        Sighting existing = sightingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sighting not found"));

        existing.setSpecies(updated.getSpecies());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setTimestamp(updated.getTimestamp());

        Sighting saved = sightingRepository.save(existing);
        
        // Update statistics in case species changed
        if (existing.getUser() != null) {
            updateUserStatistics(existing.getUser().getId());
        }
        
        return saved;
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
    	Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sighting not found"));
        
        Long userId = sighting.getUser() != null ? sighting.getUser().getId() : null;
        
        sightingRepository.deleteById(id);
        
        // Update statistics after deletion
        if (userId != null) {
            updateUserStatistics(userId);
        }
    }
    
    // Update user statistics based on their sightings
    private void updateUserStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Sighting> userSightings = sightingRepository.findByUserId(userId);
        
        // Update total animals logged
        user.setTotalAnimalsLogged(userSightings.size());
        
        // Update unique species count
        long uniqueSpecies = userSightings.stream()
                .map(Sighting::getSpecies)
                .distinct()
                .count();
        user.setUniqueSpeciesCount((int) uniqueSpecies);
        
        // Update last activity date
        user.setLastActivityDate(LocalDateTime.now());
        
        userRepository.save(user);
    }
    
    /**
     * Converts Sighting entity to SightingResponse DTO.
     */
    public SightingResponse toSightingResponse(Sighting sighting) {
        return new SightingResponse(
            sighting.getId(),
            sighting.getSpecies(),
            sighting.getLocation(),
            sighting.getDescription(),
            sighting.getImageUrl(),
            sighting.getTimestamp(),
            sighting.getPixelX(),
            sighting.getPixelY(),
            sighting.getUser() != null ? sighting.getUser().getId() : null,
            sighting.getUser() != null ? sighting.getUser().getUsername() : null
        );
    }
}
