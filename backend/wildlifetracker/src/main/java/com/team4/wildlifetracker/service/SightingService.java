package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.SightingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;

    public SightingService(SightingRepository sightingRepository) {
        this.sightingRepository = sightingRepository;
    }

    public Sighting createSighting(String species, String location, String description, String imageUrl, User user) {
        Sighting sighting = new Sighting(species, location, description, imageUrl, user);
        return sightingRepository.save(sighting);
    }

    public List<Sighting> getAllSightings() {
        return sightingRepository.findAll();
    }

    public Optional<Sighting> getSightingById(Long id) {
        return sightingRepository.findById(id);
    }

    public List<Sighting> getSightingsByUser(Long userId) {
        return sightingRepository.findByUserId(userId);
    }

    public List<Sighting> getSightingsBySpecies(String species) {
        return sightingRepository.findBySpecies(species);
    }
}
