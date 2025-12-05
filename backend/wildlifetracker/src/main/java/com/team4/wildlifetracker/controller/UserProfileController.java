package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.dto.SightingResponse;
import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.service.LeaderboardService;
import com.team4.wildlifetracker.service.SightingService;
import com.team4.wildlifetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;
    private final LeaderboardService leaderboardService;
    private final SightingService sightingService;

    public UserProfileController(UserService userService, LeaderboardService leaderboardService, SightingService sightingService) {
        this.userService = userService;
        this.leaderboardService = leaderboardService;
        this.sightingService = sightingService;
    }

    // GET user profile by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<UserResponse> user = userService.findByIdAsDto(userId);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Get user's leaderboard rank
        var leaderboardEntry = leaderboardService.getUserRank(userId);

        // Get user's sightings
        List<SightingResponse> userSightings = sightingService.findAllAsDto().stream()
                .filter(s -> s.getUserId() != null && s.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());

        // Calculate statistics
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSightings", userSightings.size());
        statistics.put("uniqueSpecies", (int) userSightings.stream()
                .map(SightingResponse::getSpecies)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .count());
        
        // Find favorite species
        String favoriteSpecies = userSightings.stream()
                .map(SightingResponse::getSpecies)
                .filter(s -> s != null && !s.isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(s -> s, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        long favoriteSpeciesCount = favoriteSpecies != null ? userSightings.stream()
                .filter(s -> favoriteSpecies.equals(s.getSpecies()))
                .count() : 0;
        
        statistics.put("favoriteSpecies", favoriteSpecies);
        statistics.put("favoriteSpeciesCount", (int) favoriteSpeciesCount);

        // Build species list
        List<Map<String, Object>> speciesList = userSightings.stream()
                .map(SightingResponse::getSpecies)
                .filter(s -> s != null && !s.isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(s -> s, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> species = new HashMap<>();
                    species.put("name", e.getKey());
                    species.put("count", e.getValue().intValue());
                    return species;
                })
                .collect(java.util.stream.Collectors.toList());

        // Return profile info with rank and sightings
        Map<String, Object> profile = new HashMap<>();
        UserResponse userDto = user.get();
        profile.put("id", userDto.getId());
        profile.put("username", userDto.getUsername());
        profile.put("displayName", userDto.getDisplayName());
        profile.put("bio", userDto.getBio());
        profile.put("profilePictureUrl", userDto.getProfilePictureUrl());
        profile.put("totalAnimalsLogged", userDto.getTotalAnimalsLogged());
        profile.put("uniqueSpeciesCount", userDto.getUniqueSpeciesCount());
        profile.put("lastActivityDate", userDto.getLastActivityDate());
        profile.put("rank", leaderboardEntry != null ? leaderboardEntry.getRank() : null);
        profile.put("statistics", statistics);
        profile.put("speciesList", speciesList);
        profile.put("recentSightings", userSightings);
        profile.put("photos", userSightings.stream()
                .filter(s -> s.getImageUrl() != null && !s.getImageUrl().isEmpty())
                .map(s -> {
                    Map<String, Object> photo = new HashMap<>();
                    photo.put("imageUrl", s.getImageUrl());
                    photo.put("species", s.getSpecies());
                    return photo;
                })
                .collect(java.util.stream.Collectors.toList()));

        return ResponseEntity.ok(profile);
    }

    // UPDATE user profile
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest request) {
        try {
            UserResponse updatedUser = userService.updateProfile(userId, request);
            
            // Get updated rank
            var leaderboardEntry = leaderboardService.getUserRank(userId);

            // Return updated profile
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", updatedUser.getId());
            profile.put("username", updatedUser.getUsername());
            profile.put("displayName", updatedUser.getDisplayName());
            profile.put("bio", updatedUser.getBio());
            profile.put("profilePictureUrl", updatedUser.getProfilePictureUrl());
            profile.put("totalAnimalsLogged", updatedUser.getTotalAnimalsLogged());
            profile.put("uniqueSpeciesCount", updatedUser.getUniqueSpeciesCount());
            profile.put("lastActivityDate", updatedUser.getLastActivityDate());
            profile.put("rank", leaderboardEntry != null ? leaderboardEntry.getRank() : null);

            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UPLOAD profile picture
    @PostMapping("/{userId}/upload-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = userService.uploadProfilePicture(userId, file);

            Map<String, String> response = new HashMap<>();
            response.put("profilePictureUrl", fileUrl);
            response.put("message", "Profile picture uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}