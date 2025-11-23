package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.repository.SightingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;

    public SightingService(SightingRepository sightingRepository) {
        this.sightingRepository = sightingRepository;
    }

    // CREATE
    public Sighting createSighting(Sighting sighting) {
        return sightingRepository.save(sighting);
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
    public Sighting update(Long id, Sighting updated) {
        Sighting existing = sightingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sighting not found"));

        existing.setSpecies(updated.getSpecies());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setTimestamp(updated.getTimestamp());

        return sightingRepository.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        if (!sightingRepository.existsById(id)) {
            throw new RuntimeException("Sighting not found");
        }
        sightingRepository.deleteById(id);
    }
}
