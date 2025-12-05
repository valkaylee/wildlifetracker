package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.LeaderboardEntry;
import com.team4.wildlifetracker.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // Get full leaderboard
    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    // Get top N users
    @GetMapping("/top/{n}")
    public ResponseEntity<List<LeaderboardEntry>> getTopN(@PathVariable int n) {
        if (n <= 0 || n > 100) {
            return ResponseEntity.badRequest().build();
        }
        List<LeaderboardEntry> topUsers = leaderboardService.getTopN(n);
        return ResponseEntity.ok(topUsers);
    }

    // Get specific user's rank
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRank(@PathVariable Long userId) {
        LeaderboardEntry entry = leaderboardService.getUserRank(userId);
        
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(entry);
    }
}