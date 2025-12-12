package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.dto.UserResponse;
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
        Optional<UserResponse> user = userService.findByIdAsDto(userId);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Get user's leaderboard rank
        var leaderboardEntry = leaderboardService.getUserRank(userId);

        // Return profile info with rank
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
}