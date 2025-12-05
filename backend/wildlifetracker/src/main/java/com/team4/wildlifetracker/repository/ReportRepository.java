package com.team4.wildlifetracker.repository;

import com.team4.wildlifetracker.model.Report;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for Report entity operations.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * Find all reports for a specific sighting.
     */
    List<Report> findBySighting(Sighting sighting);
    
    /**
     * Find all reports by sighting ID.
     */
    List<Report> findBySightingId(Long sightingId);
    
    /**
     * Find all reports submitted by a specific user.
     */
    List<Report> findByUser(User user);
    
    /**
     * Find all reports submitted by user ID.
     */
    List<Report> findByUserId(Long userId);
    
    /**
     * Count reports for a specific sighting.
     */
    long countBySightingId(Long sightingId);
    
    /**
     * Check if a user has already reported a sighting.
     */
    boolean existsBySightingIdAndUserId(Long sightingId, Long userId);
    
    /**
     * Find most reported sightings.
     */
    @Query("SELECT r.sighting, COUNT(r) as reportCount FROM Report r GROUP BY r.sighting ORDER BY reportCount DESC")
    List<Object[]> findMostReportedSightings();
}
