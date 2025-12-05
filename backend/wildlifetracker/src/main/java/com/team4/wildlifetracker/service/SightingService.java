package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.SightingRepository;
import com.team4.wildlifetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;
    private final UserRepository userRepository;

    public SightingService(SightingRepository sightingRepository, UserRepository userRepository) {
        this.sightingRepository = sightingRepository;
        this.userRepository = userRepository;
    }

    // CREATE
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

    // READ (all)
    public List<Sighting> findAll() {
        return sightingRepository.findAll();
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
}
