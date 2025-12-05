package com.team4.wildlifetracker.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team4.wildlifetracker.dto.SightingRequest;
import com.team4.wildlifetracker.dto.SightingResponse;
import com.team4.wildlifetracker.service.SightingService;

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
    
    // READ (by user)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SightingResponse>> getSightingsByUser(@PathVariable Long userId) {
        List<SightingResponse> sightings = sightingService.findByUserIdAsDto(userId);
        return ResponseEntity.ok(sightings);
    }

    // UPLOAD sighting image
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadSightingImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = sightingService.uploadSightingImage(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Image uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
