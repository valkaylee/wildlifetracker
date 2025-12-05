package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;
    private Notification savedNotification;

    // Helper method to set ID using reflection
    private void setId(Object obj, Long id) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123");
        setId(testUser, 1L);

        testNotification = new Notification("Welcome to Wildlife Tracker!", testUser);
        savedNotification = new Notification("Welcome to Wildlife Tracker!", testUser);
        setId(savedNotification, 1L);
    }

    // ==================== CREATE NOTIFICATION TESTS ====================

    @Test
    void testCreateNotification_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        Notification result = notificationService.createNotification(testUser, "Welcome to Wildlife Tracker!");

        // Assert
        assertNotNull(result);
        assertEquals("Welcome to Wildlife Tracker!", result.getMessage());
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRead()); // Should default to false
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_EmptyMessage() {
        // Arrange
        Notification emptyNotification = new Notification("", testUser);
        setId(emptyNotification, 1L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(emptyNotification);

        // Act
        Notification result = notificationService.createNotification(testUser, "");

        // Assert
        assertNotNull(result);
        assertEquals("", result.getMessage());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_NullMessage() {
        // Arrange
        Notification notificationWithNullMessage = new Notification(null, testUser);
        setId(notificationWithNullMessage, 1L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithNullMessage);

        // Act
        Notification result = notificationService.createNotification(testUser, null);

        // Assert
        assertNotNull(result);
        assertNull(result.getMessage());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_LongMessage() {
        // Arrange
        String longMessage = "A".repeat(1000);
        Notification longNotification = new Notification(longMessage, testUser);
        setId(longNotification, 1L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(longNotification);

        // Act
        Notification result = notificationService.createNotification(testUser, longMessage);

        // Assert
        assertNotNull(result);
        assertEquals(longMessage, result.getMessage());
        verify(notificationRepository).save(any(Notification.class));
    }

    // ==================== GET USER NOTIFICATIONS TESTS ====================

    @Test
    void testGetUserNotifications_Success() {
        // Arrange
        Notification notification1 = new Notification("Notification 1", testUser);
        setId(notification1, 1L);
        Notification notification2 = new Notification("Notification 2", testUser);
        setId(notification2, 2L);
        List<Notification> notifications = Arrays.asList(notification2, notification1); // Ordered by timestamp desc
        
        when(notificationRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.getUserNotifications(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationRepository).findByUserIdOrderByTimestampDesc(1L);
    }

    @Test
    void testGetUserNotifications_EmptyList() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of());

        // Act
        List<Notification> result = notificationService.getUserNotifications(testUser);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRepository).findByUserIdOrderByTimestampDesc(1L);
    }

    @Test
    void testGetUserNotifications_MultipleUsers() {
        // Arrange
        User user2 = new User("user2", "password");
        setId(user2, 2L);
        Notification user2Notification = new Notification("User 2 notification", user2);
        
        when(notificationRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of(savedNotification));
        when(notificationRepository.findByUserIdOrderByTimestampDesc(2L)).thenReturn(List.of(user2Notification));

        // Act
        List<Notification> user1Notifications = notificationService.getUserNotifications(testUser);
        List<Notification> user2Notifications = notificationService.getUserNotifications(user2);

        // Assert
        assertEquals(1, user1Notifications.size());
        assertEquals(1, user2Notifications.size());
        assertEquals("Welcome to Wildlife Tracker!", user1Notifications.get(0).getMessage());
        assertEquals("User 2 notification", user2Notifications.get(0).getMessage());
    }

    // ==================== MARK AS READ TESTS ====================

    @Test
    void testMarkAsRead_Success() {
        // Arrange
        savedNotification.setRead(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(savedNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificationService.markAsRead(1L);

        // Assert
        assertTrue(savedNotification.isRead());
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(savedNotification);
    }

    @Test
    void testMarkAsRead_AlreadyRead() {
        // Arrange
        savedNotification.setRead(true);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(savedNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificationService.markAsRead(1L);

        // Assert
        assertTrue(savedNotification.isRead());
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(savedNotification);
    }

    @Test
    void testMarkAsRead_NotificationNotFound() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        notificationService.markAsRead(999L);

        // Assert
        verify(notificationRepository).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testMarkAsRead_MultipleNotifications() {
        // Arrange
        Notification notification1 = new Notification("Notification 1", testUser);
        setId(notification1, 1L);
        notification1.setRead(false);
        
        Notification notification2 = new Notification("Notification 2", testUser);
        setId(notification2, 2L);
        notification2.setRead(false);
        
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification2));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        notificationService.markAsRead(1L);
        notificationService.markAsRead(2L);

        // Assert
        assertTrue(notification1.isRead());
        assertTrue(notification2.isRead());
        verify(notificationRepository, times(2)).findById(anyLong());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    // ==================== EDGE CASES ====================

    @Test
    void testCreateNotification_WithNullUser() {
        // Arrange
        Notification notificationWithNullUser = new Notification("Message", null);
        setId(notificationWithNullUser, 1L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithNullUser);

        // Act
        Notification result = notificationService.createNotification(null, "Message");

        // Assert
        assertNotNull(result);
        assertNull(result.getUser());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications_UnreadAndReadMix() {
        // Arrange
        Notification unreadNotification = new Notification("Unread", testUser);
        setId(unreadNotification, 1L);
        unreadNotification.setRead(false);
        
        Notification readNotification = new Notification("Read", testUser);
        setId(readNotification, 2L);
        readNotification.setRead(true);
        
        List<Notification> notifications = Arrays.asList(readNotification, unreadNotification);
        when(notificationRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.getUserNotifications(testUser);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(n -> n.isRead()));
        assertTrue(result.stream().anyMatch(n -> !n.isRead()));
    }
}

