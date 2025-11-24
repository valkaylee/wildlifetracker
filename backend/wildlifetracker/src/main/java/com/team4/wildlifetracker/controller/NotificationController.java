package com.team4.wildlifetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // TODO (Brian): Implement notification system
    @GetMapping
    public ResponseEntity<?> notificationsPlaceholder() {
        return ResponseEntity.ok("Notifications endpoint placeholder - ready to implement.");
    }
}
