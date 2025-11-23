package com.team4.wildlifetracker.repository;

import com.team4.wildlifetracker.model.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SightingRepository extends JpaRepository<Sighting, Long> {
    List<Sighting> findBySpecies(String species);
    List<Sighting> findByUserId(Long userId);
}
