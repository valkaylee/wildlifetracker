package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.SightingResponse;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.repository.SightingRepository;
import com.team4.wildlifetracker.service.SightingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SightingRepository sightingRepository;
    
    @Autowired
    private SightingService sightingService;

    @GetMapping
    public ResponseEntity<List<SightingResponse>> searchSightings(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Sighting> speciesMatches = sightingRepository.findBySpeciesContainingIgnoreCase(query);
        List<Sighting> locationMatches = sightingRepository.findByLocationContainingIgnoreCase(query);

        Set<Sighting> combinedResults = new HashSet<>();
        combinedResults.addAll(speciesMatches);
        combinedResults.addAll(locationMatches);
        
        // Convert to DTOs
        List<SightingResponse> responseList = combinedResults.stream()
                .map(sightingService::toSightingResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}
