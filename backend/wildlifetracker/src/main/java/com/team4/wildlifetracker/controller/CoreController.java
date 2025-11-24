package com.team4.wildlifetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core")
public class CoreController {

    // TODO: Wire core classes into the backend once implemented
    @GetMapping
    public ResponseEntity<?> corePlaceholder() {
        return ResponseEntity.ok("Core system endpoint placeholder, ready for core feature integration.");
    }
}
