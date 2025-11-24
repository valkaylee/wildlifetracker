package com.team4.wildlifetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    // TODO (Brian): Implement search filters, species/location queries
    @GetMapping
    public ResponseEntity<?> searchPlaceholder() {
        return ResponseEntity.ok("Search endpoint placeholder - ready to implement.");
    }
}
