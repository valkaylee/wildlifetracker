package com.team4.wildlifetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    // TODO (Sriya): Implement profile info (bio, display name, etc.)
    @GetMapping
    public ResponseEntity<?> profilePlaceholder() {
        return ResponseEntity.ok("User profile endpoint placeholder - ready to implement.");
    }
}
