package com.team4.wildlifetracker.repository;

import com.team4.wildlifetracker.model.Profile;
import com.team4.wildlifetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Profile entity operations.
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    /**
     * Find a profile by user ID.
     */
    Optional<Profile> findByUserId(Long userId);
    
    /**
     * Find a profile by user entity.
     */
    Optional<Profile> findByUser(User user);
    
    /**
     * Find top profiles ordered by animals logged.
     */
    @Query("SELECT p FROM Profile p ORDER BY p.animalsLogged DESC")
    List<Profile> findTopByAnimalsLogged();
    
    /**
     * Find top profiles ordered by species seen.
     */
    @Query("SELECT p FROM Profile p ORDER BY p.speciesSeen DESC")
    List<Profile> findTopBySpeciesSeen();
    
    /**
     * Find top profiles ordered by assists.
     */
    @Query("SELECT p FROM Profile p ORDER BY p.assists DESC")
    List<Profile> findTopByAssists();
}
