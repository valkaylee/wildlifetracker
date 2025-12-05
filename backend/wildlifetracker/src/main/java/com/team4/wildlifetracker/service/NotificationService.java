package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.NotificationResponse;
import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(User user, String message) {
        Notification notification = new Notification(message, user);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(user.getId());
    }
    
    public List<NotificationResponse> getUserNotificationsAsDto(User user) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(user.getId()).stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    /**
     * Converts Notification entity to NotificationResponse DTO.
     */
    public NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getMessage(),
            notification.getTimestamp(),
            notification.isRead(),
            notification.getUser() != null ? notification.getUser().getId() : null
        );
    }
}
