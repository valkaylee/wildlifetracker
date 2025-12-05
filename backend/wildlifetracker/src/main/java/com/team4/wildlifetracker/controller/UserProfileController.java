package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.service.LeaderboardService;
import com.team4.wildlifetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;
    private final LeaderboardService leaderboardService;

    public UserProfileController(UserService userService, LeaderboardService leaderboardService) {
        this.userService = userService;
        this.leaderboardService = leaderboardService;
    }

    // GET user profile by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Get user's leaderboard rank
        var leaderboardEntry = leaderboardService.getUserRank(userId);

        // Return profile info (excluding password)
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.get().getId());
        profile.put("username", user.get().getUsername());
        profile.put("displayName", user.get().getDisplayName());
        profile.put("bio", user.get().getBio());
        profile.put("profilePictureUrl", user.get().getProfilePictureUrl());
        profile.put("totalAnimalsLogged", user.get().getTotalAnimalsLogged());
        profile.put("uniqueSpeciesCount", user.get().getUniqueSpeciesCount());
        profile.put("lastActivityDate", user.get().getLastActivityDate());
        profile.put("rank", leaderboardEntry != null ? leaderboardEntry.getRank() : null);

        return ResponseEntity.ok(profile);
    }

    // UPDATE user profile
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest request) {
        try {
            User updatedUser = userService.updateProfile(userId, request);
            
            // Get updated rank
            var leaderboardEntry = leaderboardService.getUserRank(userId);

            // Return updated profile (excluding password)
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