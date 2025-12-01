package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.repository.SightingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SightingRepository sightingRepository;

    @GetMapping
    public ResponseEntity<List<Sighting>> searchSightings(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Sighting> speciesMatches = sightingRepository.findBySpeciesContainingIgnoreCase(query);
        List<Sighting> locationMatches = sightingRepository.findByLocationContainingIgnoreCase(query);

        Set<Sighting> combinedResults = new HashSet<>();
        combinedResults.addAll(speciesMatches);
        combinedResults.addAll(locationMatches);

        return ResponseEntity.ok(new ArrayList<>(combinedResults));
    }
}
