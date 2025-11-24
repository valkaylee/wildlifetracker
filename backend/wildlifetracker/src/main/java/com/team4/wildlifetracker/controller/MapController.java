package com.team4.wildlifetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/map")
public class MapController {

    // TODO: Add mapping/geo endpoints if needed later
    @GetMapping
    public ResponseEntity<?> mapPlaceholder() {
        return ResponseEntity.ok("Map endpoint placeholder, ready for future integration.");
    }
}
