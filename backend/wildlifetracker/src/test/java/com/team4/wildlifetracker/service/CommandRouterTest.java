package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.Command;
import com.team4.wildlifetracker.dto.CommandResponse;
import com.team4.wildlifetracker.dto.LeaderboardEntry;
import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandRouterTest {

    @Mock
    private UserService userService;

    @Mock
    private SightingService sightingService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private CommandRouter commandRouter;

    private User testUser;
    private Sighting testSighting;
    private Notification testNotification;

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
        testUser = new User("testuser", "password");
        setId(testUser, 1L);

        testSighting = new Sighting("Gray Wolf", "Yellowstone", "Description", "url", testUser);
        setId(testSighting, 1L);

        testNotification = new Notification("Test notification", testUser);
        setId(testNotification, 1L);
    }

    // ==================== INVALID COMMAND TESTS ====================

    @Test
    void testRoute_NullCommand() {
        // Act
        CommandResponse result = commandRouter.route(null);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("Invalid command"));
    }

    @Test
    void testRoute_NullCommandType() {
        // Arrange
        Command command = new Command();
        command.setAction("get");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Invalid command"));
    }

    @Test
    void testRoute_NullAction() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Invalid command"));
    }

    @Test
    void testRoute_UnknownCommandType() {
        // Arrange
        Command command = new Command();
        command.setCommandType("unknown");
        command.setAction("get");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Unknown command type"));
    }

    // ==================== USER COMMAND TESTS ====================

    @Test
    void testRoute_UserGet_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("User retrieved", result.getMessage());
        assertEquals(testUser, result.getData());
        verify(userService).findById(1L);
    }

    @Test
    void testRoute_UserGet_MissingUserId() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("userId is required"));
    }

    @Test
    void testRoute_UserGet_UserNotFound() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 999L);
        command.setParameters(params);

        when(userService.findById(999L)).thenReturn(Optional.empty());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("User not found"));
    }

    @Test
    void testRoute_UserGet_UnknownAction() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("unknown");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Unknown user action"));
    }

    // ==================== SIGHTING COMMAND TESTS ====================

    @Test
    void testRoute_SightingCreate_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("create");
        Map<String, Object> params = new HashMap<>();
        params.put("species", "Gray Wolf");
        params.put("location", "Yellowstone");
        params.put("description", "Description");
        params.put("imageUrl", "url");
        params.put("userId", 1L);
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingService.createSighting(any(Sighting.class))).thenReturn(testSighting);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Sighting created", result.getMessage());
        assertNotNull(result.getData());
        verify(sightingService).createSighting(any(Sighting.class));
    }

    @Test
    void testRoute_SightingGet_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        command.setParameters(params);

        when(sightingService.findById(1L)).thenReturn(testSighting);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Sighting retrieved", result.getMessage());
        assertEquals(testSighting, result.getData());
        verify(sightingService).findById(1L);
    }

    @Test
    void testRoute_SightingGetAll_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("getall");
        command.setParameters(new HashMap<>());

        List<Sighting> sightings = List.of(testSighting);
        when(sightingService.findAll()).thenReturn(sightings);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Sightings retrieved", result.getMessage());
        assertEquals(sightings, result.getData());
        verify(sightingService).findAll();
    }

    @Test
    void testRoute_SightingUpdate_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("update");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        params.put("species", "Red Wolf");
        params.put("location", "North Carolina");
        params.put("description", "Updated");
        params.put("imageUrl", "url2");
        command.setParameters(params);

        when(sightingService.update(anyLong(), any(Sighting.class))).thenReturn(testSighting);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Sighting updated", result.getMessage());
        verify(sightingService).update(eq(1L), any(Sighting.class));
    }

    @Test
    void testRoute_SightingDelete_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("delete");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        command.setParameters(params);

        doNothing().when(sightingService).delete(1L);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Sighting deleted", result.getMessage());
        verify(sightingService).delete(1L);
    }

    @Test
    void testRoute_SightingGet_MissingId() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("get");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Sighting id is required"));
    }

    // ==================== NOTIFICATION COMMAND TESTS ====================

    @Test
    void testRoute_NotificationGet_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("notification");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationService.getUserNotifications(testUser)).thenReturn(List.of(testNotification));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Notifications retrieved", result.getMessage());
        assertNotNull(result.getData());
        verify(notificationService).getUserNotifications(testUser);
    }

    @Test
    void testRoute_NotificationMarkRead_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("notification");
        command.setAction("markread");
        Map<String, Object> params = new HashMap<>();
        params.put("notificationId", 1L);
        command.setParameters(params);

        doNothing().when(notificationService).markAsRead(1L);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Notification marked as read", result.getMessage());
        verify(notificationService).markAsRead(1L);
    }

    @Test
    void testRoute_NotificationCreate_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("notification");
        command.setAction("create");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        params.put("message", "New notification");
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationService.createNotification(testUser, "New notification")).thenReturn(testNotification);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Notification created", result.getMessage());
        verify(notificationService).createNotification(testUser, "New notification");
    }

    @Test
    void testRoute_NotificationCreate_MissingParameters() {
        // Arrange
        Command command = new Command();
        command.setCommandType("notification");
        command.setAction("create");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        // Missing message
        command.setParameters(params);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("userId and message are required"));
    }

    // ==================== LEADERBOARD COMMAND TESTS ====================

    @Test
    void testRoute_LeaderboardGet_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("leaderboard");
        command.setAction("get");
        command.setParameters(new HashMap<>());

        LeaderboardEntry entry = new LeaderboardEntry(1L, "user1", "User One", null, 10, 5, LocalDateTime.now());
        entry.setRank(1);
        List<LeaderboardEntry> leaderboard = List.of(entry);
        when(leaderboardService.getLeaderboard()).thenReturn(leaderboard);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Leaderboard retrieved", result.getMessage());
        assertEquals(leaderboard, result.getData());
        verify(leaderboardService).getLeaderboard();
    }

    @Test
    void testRoute_LeaderboardGetTop_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("leaderboard");
        command.setAction("gettop");
        Map<String, Object> params = new HashMap<>();
        params.put("n", 5);
        command.setParameters(params);

        LeaderboardEntry entry = new LeaderboardEntry(1L, "user1", "User One", null, 10, 5, LocalDateTime.now());
        entry.setRank(1);
        List<LeaderboardEntry> topUsers = List.of(entry);
        when(leaderboardService.getTopN(5)).thenReturn(topUsers);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Top users retrieved", result.getMessage());
        verify(leaderboardService).getTopN(5);
    }

    @Test
    void testRoute_LeaderboardGetTop_InvalidN() {
        // Arrange
        Command command = new Command();
        command.setCommandType("leaderboard");
        command.setAction("gettop");
        Map<String, Object> params = new HashMap<>();
        params.put("n", 0); // Invalid
        command.setParameters(params);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("n must be between 1 and 100"));
    }

    @Test
    void testRoute_LeaderboardGetUserRank_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("leaderboard");
        command.setAction("getuserrank");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        command.setParameters(params);

        LeaderboardEntry entry = new LeaderboardEntry(1L, "user1", "User One", null, 10, 5, LocalDateTime.now());
        entry.setRank(1);
        when(leaderboardService.getUserRank(1L)).thenReturn(entry);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("User rank retrieved", result.getMessage());
        verify(leaderboardService).getUserRank(1L);
    }

    @Test
    void testRoute_LeaderboardGetUserRank_UserNotFound() {
        // Arrange
        Command command = new Command();
        command.setCommandType("leaderboard");
        command.setAction("getuserrank");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 999L);
        command.setParameters(params);

        when(leaderboardService.getUserRank(999L)).thenReturn(null);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("User not found in leaderboard"));
    }

    // ==================== PROFILE COMMAND TESTS ====================

    @Test
    void testRoute_ProfileGet_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("profile");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(leaderboardService.getUserRank(1L)).thenReturn(null);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Profile retrieved", result.getMessage());
        assertNotNull(result.getData());
        verify(userService).findById(1L);
    }

    @Test
    void testRoute_ProfileUpdate_Success() {
        // Arrange
        Command command = new Command();
        command.setCommandType("profile");
        command.setAction("update");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        params.put("displayName", "New Name");
        params.put("bio", "New bio");
        params.put("profilePictureUrl", "/uploads/pic.jpg");
        command.setParameters(params);

        UserResponse updatedUserResponse = new UserResponse();
        updatedUserResponse.setId(1L);
        updatedUserResponse.setUsername("testuser");
        updatedUserResponse.setDisplayName("New Name");
        updatedUserResponse.setBio("New bio");
        updatedUserResponse.setProfilePictureUrl("/uploads/pic.jpg");

        when(userService.updateProfile(eq(1L), any())).thenReturn(updatedUserResponse);
        when(leaderboardService.getUserRank(1L)).thenReturn(null);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Profile updated", result.getMessage());
        verify(userService).updateProfile(eq(1L), any());
    }

    @Test
    void testRoute_ProfileUpdate_MissingUserId() {
        // Arrange
        Command command = new Command();
        command.setCommandType("profile");
        command.setAction("update");
        command.setParameters(new HashMap<>());

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("userId is required"));
    }

    // ==================== PARAMETER TYPE CONVERSION TESTS ====================

    @Test
    void testRoute_ParameterConversion_StringToLong() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "1"); // String instead of Long
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        verify(userService).findById(1L);
    }

    @Test
    void testRoute_ParameterConversion_IntegerToLong() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1); // Integer instead of Long
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
        verify(userService).findById(1L);
    }

    @Test
    void testRoute_ParameterConversion_InvalidStringToLong() {
        // Arrange
        Command command = new Command();
        command.setCommandType("user");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "invalid"); // Invalid string
        command.setParameters(params);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("userId is required"));
    }

    // ==================== CASE INSENSITIVE TESTS ====================

    @Test
    void testRoute_CaseInsensitive_UpperCase() {
        // Arrange
        Command command = new Command();
        command.setCommandType("USER");
        command.setAction("GET");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);
        command.setParameters(params);

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void testRoute_CaseInsensitive_MixedCase() {
        // Arrange
        Command command = new Command();
        command.setCommandType("SiGhTiNg");
        command.setAction("GeT");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        command.setParameters(params);

        when(sightingService.findById(1L)).thenReturn(testSighting);

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertTrue(result.isSuccess());
    }

    // ==================== EXCEPTION HANDLING TESTS ====================

    @Test
    void testRoute_ExceptionHandling_ServiceThrowsException() {
        // Arrange
        Command command = new Command();
        command.setCommandType("sighting");
        command.setAction("get");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        command.setParameters(params);

        when(sightingService.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        CommandResponse result = commandRouter.route(command);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Error executing command"));
    }
}

