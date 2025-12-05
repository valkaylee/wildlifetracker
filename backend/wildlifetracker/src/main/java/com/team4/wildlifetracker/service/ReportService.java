package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Report;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.ReportRepository;
import com.team4.wildlifetracker.repository.SightingRepository;
import com.team4.wildlifetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing reports on sightings.
 * Handles report creation, retrieval, and moderation operations.
 */
@Service
@Transactional
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    private final ReportRepository reportRepository;
    private final SightingRepository sightingRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, 
                        SightingRepository sightingRepository,
                        UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.sightingRepository = sightingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new report.
     * @param sightingId The sighting ID to report
     * @param userId The user ID submitting the report
     * @param reason The reason for the report
     * @return The created report
     * @throws IllegalArgumentException if sighting or user not found, or duplicate report
     */
    public Report createReport(Long sightingId, Long userId, String reason) {
        logger.info("Creating report for sighting {} by user {}", sightingId, userId);
        
        // Check if user has already reported this sighting
        if (reportRepository.existsBySightingIdAndUserId(sightingId, userId)) {
            throw new IllegalArgumentException("User has already reported this sighting");
        }
        
        Sighting sighting = sightingRepository.findById(sightingId)
                .orElseThrow(() -> new IllegalArgumentException("Sighting not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Report report = new Report(sighting, user, reason);
        return reportRepository.save(report);
    }

    /**
     * Get all reports for a sighting.
     * @param sightingId The sighting ID
     * @return List of reports
     */
    @Transactional(readOnly = true)
    public List<Report> getReportsBySightingId(Long sightingId) {
        return reportRepository.findBySightingId(sightingId);
    }

    /**
     * Get all reports submitted by a user.
     * @param userId The user ID
     * @return List of reports
     */
    @Transactional(readOnly = true)
    public List<Report> getReportsByUserId(Long userId) {
        return reportRepository.findByUserId(userId);
    }

    /**
     * Get report by ID.
     * @param reportId The report ID
     * @return Optional containing the report if found
     */
    @Transactional(readOnly = true)
    public Optional<Report> getReportById(Long reportId) {
        return reportRepository.findById(reportId);
    }

    /**
     * Get all reports.
     * @return List of all reports
     */
    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Count reports for a sighting.
     * @param sightingId The sighting ID
     * @return Number of reports
     */
    @Transactional(readOnly = true)
    public long countReportsBySightingId(Long sightingId) {
        return reportRepository.countBySightingId(sightingId);
    }

    /**
     * Check if a user has reported a sighting.
     * @param sightingId The sighting ID
     * @param userId The user ID
     * @return true if the user has reported the sighting
     */
    @Transactional(readOnly = true)
    public boolean hasUserReportedSighting(Long sightingId, Long userId) {
        return reportRepository.existsBySightingIdAndUserId(sightingId, userId);
    }

    /**
     * Get most reported sightings.
     * @return List of sighting-report count pairs
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostReportedSightings() {
        return reportRepository.findMostReportedSightings();
    }

    /**
     * Delete a report.
     * @param reportId The report ID
     */
    public void deleteReport(Long reportId) {
        logger.info("Deleting report: {}", reportId);
        reportRepository.deleteById(reportId);
    }

    /**
     * Delete all reports for a sighting.
     * @param sightingId The sighting ID
     */
    public void deleteReportsBySightingId(Long sightingId) {
        logger.info("Deleting all reports for sighting: {}", sightingId);
        List<Report> reports = reportRepository.findBySightingId(sightingId);
        reportRepository.deleteAll(reports);
    }
}
