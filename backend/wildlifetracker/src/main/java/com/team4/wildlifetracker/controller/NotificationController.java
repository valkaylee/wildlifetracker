package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.NotificationResponse;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.service.NotificationService;
import com.team4.wildlifetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserNotifications(@RequestParam Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<NotificationResponse> notifications = notificationService.getUserNotificationsAsDto(user.get());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
