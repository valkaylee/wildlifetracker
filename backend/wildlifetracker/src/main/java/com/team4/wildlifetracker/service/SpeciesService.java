package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Species;
import com.team4.wildlifetracker.repository.SpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing species data.
 * Handles species creation, search, and retrieval operations.
 */
@Service
@Transactional
public class SpeciesService {

    private static final Logger logger = LoggerFactory.getLogger(SpeciesService.class);
    
    private final SpeciesRepository speciesRepository;

    public SpeciesService(SpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }

    /**
     * Create a new species.
     * @param name The species name
     * @param category The species category
     * @return The created species
     * @throws IllegalArgumentException if species already exists
     */
    public Species createSpecies(String name, String category) {
        logger.info("Creating species: {} in category: {}", name, category);
        
        if (speciesRepository.existsByName(name)) {
            throw new IllegalArgumentException("Species already exists: " + name);
        }
        
        Species species = new Species(name, category);
        return speciesRepository.save(species);
    }

    /**
     * Get species by ID.
     * @param id The species ID
     * @return Optional containing the species if found
     */
    @Transactional(readOnly = true)
    public Optional<Species> getSpeciesById(Long id) {
        return speciesRepository.findById(id);
    }

    /**
     * Get species by exact name.
     * @param name The species name
     * @return Optional containing the species if found
     */
    @Transactional(readOnly = true)
    public Optional<Species> getSpeciesByName(String name) {
        return speciesRepository.findByName(name);
    }

    /**
     * Search species by name (case-insensitive partial match).
     * @param name The search term
     * @return List of matching species
     */
    @Transactional(readOnly = true)
    public List<Species> searchSpeciesByName(String name) {
        return speciesRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get all species in a category.
     * @param category The category name
     * @return List of species in the category
     */
    @Transactional(readOnly = true)
    public List<Species> getSpeciesByCategory(String category) {
        return speciesRepository.findByCategory(category);
    }

    /**
     * Search species by category (case-insensitive partial match).
     * @param category The search term
     * @return List of matching species
     */
    @Transactional(readOnly = true)
    public List<Species> searchSpeciesByCategory(String category) {
        return speciesRepository.findByCategoryContainingIgnoreCase(category);
    }

    /**
     * Get all species.
     * @return List of all species
     */
    @Transactional(readOnly = true)
    public List<Species> getAllSpecies() {
        return speciesRepository.findAll();
    }

    /**
     * Update species information.
     * @param id The species ID
     * @param name The new name (optional)
     * @param category The new category (optional)
     * @return The updated species
     * @throws IllegalArgumentException if species not found
     */
    public Species updateSpecies(Long id, String name, String category) {
        logger.info("Updating species: {}", id);
        
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Species not found"));
        
        if (name != null && !name.isEmpty()) {
            species.setName(name);
        }
        
        if (category != null && !category.isEmpty()) {
            species.setCategory(category);
        }
        
        return speciesRepository.save(species);
    }

    /**
     * Delete a species.
     * @param id The species ID
     */
    public void deleteSpecies(Long id) {
        logger.info("Deleting species: {}", id);
        speciesRepository.deleteById(id);
    }

    /**
     * Check if a species exists by name.
     * @param name The species name
     * @return true if the species exists
     */
    @Transactional(readOnly = true)
    public boolean speciesExists(String name) {
        return speciesRepository.existsByName(name);
    }

    /**
     * Get or create species by name.
     * Creates a new species if it doesn't exist.
     * @param name The species name
     * @param category The category for new species
     * @return The species (existing or newly created)
     */
    public Species getOrCreateSpecies(String name, String category) {
        return speciesRepository.findByName(name)
                .orElseGet(() -> {
                    logger.info("Species not found, creating new: {}", name);
                    return createSpecies(name, category);
                });
    }
}
