package com.team4.wildlifetracker.repository;

import com.team4.wildlifetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    // Get users ordered by leaderboard criteria
    @Query("SELECT u FROM User u ORDER BY u.totalAnimalsLogged DESC, u.uniqueSpeciesCount DESC, u.lastActivityDate DESC")
    List<User> findAllOrderedByLeaderboardRank();
}
