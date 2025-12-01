package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
