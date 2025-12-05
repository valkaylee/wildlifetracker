package com.team4.wildlifetracker.repository;

import com.team4.wildlifetracker.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Species entity operations.
 */
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    
    /**
     * Find species by exact name.
     */
    Optional<Species> findByName(String name);
    
    /**
     * Find species by name containing (case-insensitive).
     */
    List<Species> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all species in a category.
     */
    List<Species> findByCategory(String category);
    
    /**
     * Find species by category containing (case-insensitive).
     */
    List<Species> findByCategoryContainingIgnoreCase(String category);
    
    /**
     * Check if species exists by name.
     */
    boolean existsByName(String name);
}
