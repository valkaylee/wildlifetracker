package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.ReportRequest;
import com.team4.wildlifetracker.dto.ReportResponse;
import com.team4.wildlifetracker.model.Report;
import com.team4.wildlifetracker.service.ReportService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for report management endpoints.
 * Handles HTTP requests for reporting sightings and moderation.
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Create a new report.
     * POST /api/reports
     */
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        logger.info("POST request to create report for sighting: {}", request.getSightingId());
        
        try {
            Report report = reportService.createReport(
                    request.getSightingId(),
                    request.getUserId(),
                    request.getReason()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertToResponse(report));
        } catch (IllegalArgumentException e) {
            logger.error("Error creating report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all reports.
     * GET /api/reports
     */
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        logger.info("GET request to retrieve all reports");
        
        List<ReportResponse> reports = reportService.getAllReports()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Get report by ID.
     * GET /api/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long reportId) {
        logger.info("GET request to retrieve report: {}", reportId);
        
        return reportService.getReportById(reportId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get reports by sighting ID.
     * GET /api/reports/sighting/{sightingId}
     */
    @GetMapping("/sighting/{sightingId}")
    public ResponseEntity<List<ReportResponse>> getReportsBySightingId(@PathVariable Long sightingId) {
        logger.info("GET request to retrieve reports for sighting: {}", sightingId);
        
        List<ReportResponse> reports = reportService.getReportsBySightingId(sightingId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Get reports by user ID.
     * GET /api/reports/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportResponse>> getReportsByUserId(@PathVariable Long userId) {
        logger.info("GET request to retrieve reports by user: {}", userId);
        
        List<ReportResponse> reports = reportService.getReportsByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Count reports for a sighting.
     * GET /api/reports/sighting/{sightingId}/count
     */
    @GetMapping("/sighting/{sightingId}/count")
    public ResponseEntity<Long> countReportsBySightingId(@PathVariable Long sightingId) {
        logger.info("GET request to count reports for sighting: {}", sightingId);
        
        long count = reportService.countReportsBySightingId(sightingId);
        return ResponseEntity.ok(count);
    }

    /**
     * Check if user has reported a sighting.
     * GET /api/reports/check?sightingId={sightingId}&userId={userId}
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserReportedSighting(
            @RequestParam Long sightingId,
            @RequestParam Long userId) {
        logger.info("GET request to check if user {} has reported sighting {}", userId, sightingId);
        
        boolean hasReported = reportService.hasUserReportedSighting(sightingId, userId);
        return ResponseEntity.ok(hasReported);
    }

    /**
     * Get most reported sightings.
     * GET /api/reports/most-reported
     */
    @GetMapping("/most-reported")
    public ResponseEntity<List<Object[]>> getMostReportedSightings() {
        logger.info("GET request to retrieve most reported sightings");
        
        List<Object[]> results = reportService.getMostReportedSightings();
        return ResponseEntity.ok(results);
    }

    /**
     * Delete a report.
     * DELETE /api/reports/{reportId}
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        logger.info("DELETE request for report: {}", reportId);
        
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all reports for a sighting.
     * DELETE /api/reports/sighting/{sightingId}
     */
    @DeleteMapping("/sighting/{sightingId}")
    public ResponseEntity<Void> deleteReportsBySightingId(@PathVariable Long sightingId) {
        logger.info("DELETE request for all reports of sighting: {}", sightingId);
        
        reportService.deleteReportsBySightingId(sightingId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convert Report entity to ReportResponse DTO.
     */
    private ReportResponse convertToResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getSighting().getId(),
                report.getUser().getId(),
                report.getUser().getUsername(),
                report.getReason(),
                report.getTimestamp()
        );
    }
}
