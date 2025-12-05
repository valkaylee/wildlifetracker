package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.LeaderboardEntry;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        List<User> users = userRepository.findAllOrderedByLeaderboardRank();
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        
        int rank = 1;
        for (User user : users) {
            LeaderboardEntry entry = new LeaderboardEntry(
                user.getId(),
                user.getUsername(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                user.getProfilePictureUrl(),
                user.getTotalAnimalsLogged(),
                user.getUniqueSpeciesCount(),
                user.getLastActivityDate()
            );
            entry.setRank(rank++);
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }

    public List<LeaderboardEntry> getTopN(int n) {
        List<LeaderboardEntry> fullLeaderboard = getLeaderboard();
        return fullLeaderboard.subList(0, Math.min(n, fullLeaderboard.size()));
    }

    public LeaderboardEntry getUserRank(Long userId) {
        List<LeaderboardEntry> leaderboard = getLeaderboard();
        
        return leaderboard.stream()
                .filter(entry -> entry.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}