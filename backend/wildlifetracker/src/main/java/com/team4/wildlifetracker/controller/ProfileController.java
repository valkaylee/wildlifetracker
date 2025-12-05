package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.ProfileResponse;
import com.team4.wildlifetracker.model.Profile;
import com.team4.wildlifetracker.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for profile management endpoints.
 * Handles HTTP requests for profile operations.
 */
@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Get profile by user ID.
     * GET /api/profiles/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        logger.info("GET request to retrieve profile for user: {}", userId);
        
        return profileService.getProfileByUserId(userId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all profiles.
     * GET /api/profiles
     */
    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAllProfiles() {
        logger.info("GET request to retrieve all profiles");
        
        List<ProfileResponse> profiles = profileService.getAllProfiles()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profiles);
    }

    /**
     * Get top profiles by animals logged.
     * GET /api/profiles/top/animals-logged
     */
    @GetMapping("/top/animals-logged")
    public ResponseEntity<List<ProfileResponse>> getTopProfilesByAnimalsLogged() {
        logger.info("GET request to retrieve top profiles by animals logged");
        
        List<ProfileResponse> profiles = profileService.getTopProfilesByAnimalsLogged()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profiles);
    }

    /**
     * Get top profiles by species seen.
     * GET /api/profiles/top/species-seen
     */
    @GetMapping("/top/species-seen")
    public ResponseEntity<List<ProfileResponse>> getTopProfilesBySpeciesSeen() {
        logger.info("GET request to retrieve top profiles by species seen");
        
        List<ProfileResponse> profiles = profileService.getTopProfilesBySpeciesSeen()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profiles);
    }

    /**
     * Get top profiles by assists.
     * GET /api/profiles/top/assists
     */
    @GetMapping("/top/assists")
    public ResponseEntity<List<ProfileResponse>> getTopProfilesByAssists() {
        logger.info("GET request to retrieve top profiles by assists");
        
        List<ProfileResponse> profiles = profileService.getTopProfilesByAssists()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profiles);
    }

    /**
     * Update most frequent area for a profile.
     * PATCH /api/profiles/{userId}/area
     */
    @PatchMapping("/{userId}/area")
    public ResponseEntity<Void> updateMostFrequentArea(
            @PathVariable Long userId,
            @RequestParam String area) {
        logger.info("PATCH request to update most frequent area for user: {}", userId);
        
        profileService.updateMostFrequentArea(userId, area);
        return ResponseEntity.ok().build();
    }

    /**
     * Increment animals logged counter.
     * POST /api/profiles/{userId}/increment-animals
     */
    @PostMapping("/{userId}/increment-animals")
    public ResponseEntity<Void> incrementAnimalsLogged(@PathVariable Long userId) {
        logger.info("POST request to increment animals logged for user: {}", userId);
        
        profileService.incrementAnimalsLogged(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Increment assists counter.
     * POST /api/profiles/{userId}/increment-assists
     */
    @PostMapping("/{userId}/increment-assists")
    public ResponseEntity<Void> incrementAssists(@PathVariable Long userId) {
        logger.info("POST request to increment assists for user: {}", userId);
        
        profileService.incrementAssists(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a profile.
     * DELETE /api/profiles/{profileId}
     */
    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        logger.info("DELETE request for profile: {}", profileId);
        
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convert Profile entity to ProfileResponse DTO.
     */
    private ProfileResponse convertToResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getUsername(),
                profile.getAnimalsLogged(),
                profile.getSpeciesSeen(),
                profile.getAssists(),
                profile.getMostFrequentArea()
        );
    }
}
