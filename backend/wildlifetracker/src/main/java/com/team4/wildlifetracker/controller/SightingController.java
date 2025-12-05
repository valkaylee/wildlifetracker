package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.SightingRequest;
import com.team4.wildlifetracker.dto.SightingResponse;
import com.team4.wildlifetracker.service.SightingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sightings")
@CrossOrigin(origins = "*")
public class SightingController {

    private final SightingService sightingService;

    public SightingController(SightingService sightingService) {
        this.sightingService = sightingService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<SightingResponse> createSighting(@RequestBody SightingRequest request) {
        try {
            SightingResponse response = sightingService.createSighting(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // READ (single)
    @GetMapping("/{id}")
    public ResponseEntity<SightingResponse> getSighting(@PathVariable Long id) {
        try {
            SightingResponse response = sightingService.findByIdAsDto(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // READ (all)
    @GetMapping
    public ResponseEntity<List<SightingResponse>> getAllSightings() {
        List<SightingResponse> sightings = sightingService.findAllAsDto();
        return ResponseEntity.ok(sightings);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SightingResponse> updateSighting(@PathVariable Long id, @RequestBody SightingRequest request) {
        try {
            // For now, we'll use the entity-based update and convert the result
            // This would ideally be refactored to accept SightingRequest in the service
            var existing = sightingService.findById(id);
            existing.setSpecies(request.getSpecies());
            existing.setLocation(request.getLocation());
            existing.setDescription(request.getDescription());
            existing.setImageUrl(request.getImageUrl());
            
            var updated = sightingService.update(id, existing);
            SightingResponse response = sightingService.toSightingResponse(updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSighting(@PathVariable Long id) {
        try {
            sightingService.delete(id);
            return ResponseEntity.ok("Deleted sighting " + id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
