package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.Command;
import com.team4.wildlifetracker.dto.CommandResponse;
import com.team4.wildlifetracker.dto.LeaderboardEntry;
import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central router for all commands in the application.
 * Routes commands to appropriate service handlers based on command type and action.
 * 
 * Note: This router delegates to existing services and does not interfere with:
 * - Core classes (handled separately)
 * - Authentication/Authorization (handled by AuthController)
 * - Search/Notification managers (use existing services)
 */
@Service
public class CommandRouter {

    private final UserService userService;
    private final SightingService sightingService;
    private final NotificationService notificationService;
    private final LeaderboardService leaderboardService;

    public CommandRouter(
            UserService userService,
            SightingService sightingService,
            NotificationService notificationService,
            LeaderboardService leaderboardService) {
        this.userService = userService;
        this.sightingService = sightingService;
        this.notificationService = notificationService;
        this.leaderboardService = leaderboardService;
    }

    /**
     * Routes a command to the appropriate handler based on command type and action.
     *
     * @param command The command to execute
     * @return CommandResponse containing the result
     */
    public CommandResponse route(Command command) {
        if (command == null || command.getCommandType() == null || command.getAction() == null) {
            return CommandResponse.error("Invalid command: commandType and action are required");
        }

        String commandType = command.getCommandType().toLowerCase();
        String action = command.getAction().toLowerCase();

        try {
            return switch (commandType) {
                case "user" -> handleUserCommand(action, command);
                case "sighting" -> handleSightingCommand(action, command);
                case "notification" -> handleNotificationCommand(action, command);
                case "leaderboard" -> handleLeaderboardCommand(action, command);
                case "profile" -> handleProfileCommand(action, command);
                default -> CommandResponse.error("Unknown command type: " + commandType);
            };
        } catch (Exception e) {
            return CommandResponse.error("Error executing command: " + e.getMessage());
        }
    }

    // ==================== USER COMMANDS ====================
    private CommandResponse handleUserCommand(String action, Command command) {
        Map<String, Object> params = command.getParameters();
        
        return switch (action) {
            case "get" -> {
                Long userId = getLongParam(params, "userId");
                if (userId == null) {
                    yield CommandResponse.error("userId is required");
                }
                Optional<User> user = userService.findById(userId);
                if (user.isPresent()) {
                    yield CommandResponse.success("User retrieved", user.get());
                } else {
                    yield CommandResponse.error("User not found");
                }
            }
            default -> CommandResponse.error("Unknown user action: " + action);
        };
    }

    // ==================== SIGHTING COMMANDS ====================
    private CommandResponse handleSightingCommand(String action, Command command) {
        Map<String, Object> params = command.getParameters();
        
        return switch (action) {
            case "create" -> {
                Sighting sighting = createSightingFromParams(params);
                if (sighting == null) {
                    yield CommandResponse.error("Invalid sighting data");
                }
                Sighting created = sightingService.createSighting(sighting);
                yield CommandResponse.success("Sighting created", created);
            }
            case "get" -> {
                Long id = getLongParam(params, "id");
                if (id == null) {
                    yield CommandResponse.error("Sighting id is required");
                }
                Sighting sighting = sightingService.findById(id);
                yield CommandResponse.success("Sighting retrieved", sighting);
            }
            case "getall" -> {
                List<Sighting> sightings = sightingService.findAll();
                yield CommandResponse.success("Sightings retrieved", sightings);
            }
            case "update" -> {
                Long id = getLongParam(params, "id");
                if (id == null) {
                    yield CommandResponse.error("Sighting id is required");
                }
                Sighting updated = createSightingFromParams(params);
                if (updated == null) {
                    yield CommandResponse.error("Invalid sighting data");
                }
                Sighting result = sightingService.update(id, updated);
                yield CommandResponse.success("Sighting updated", result);
            }
            case "delete" -> {
                Long id = getLongParam(params, "id");
                if (id == null) {
                    yield CommandResponse.error("Sighting id is required");
                }
                sightingService.delete(id);
                yield CommandResponse.success("Sighting deleted", null);
            }
            default -> CommandResponse.error("Unknown sighting action: " + action);
        };
    }

    // ==================== NOTIFICATION COMMANDS ====================
    private CommandResponse handleNotificationCommand(String action, Command command) {
        Map<String, Object> params = command.getParameters();
        
        return switch (action) {
            case "get" -> {
                Long userId = getLongParam(params, "userId");
                if (userId == null) {
                    yield CommandResponse.error("userId is required");
                }
                Optional<User> user = userService.findById(userId);
                if (user.isEmpty()) {
                    yield CommandResponse.error("User not found");
                }
                List<Notification> notifications = notificationService.getUserNotifications(user.get());
                yield CommandResponse.success("Notifications retrieved", notifications);
            }
            case "markread" -> {
                Long notificationId = getLongParam(params, "notificationId");
                if (notificationId == null) {
                    yield CommandResponse.error("notificationId is required");
                }
                notificationService.markAsRead(notificationId);
                yield CommandResponse.success("Notification marked as read", null);
            }
            case "create" -> {
                Long userId = getLongParam(params, "userId");
                String message = (String) params.get("message");
                if (userId == null || message == null) {
                    yield CommandResponse.error("userId and message are required");
                }
                Optional<User> user = userService.findById(userId);
                if (user.isEmpty()) {
                    yield CommandResponse.error("User not found");
                }
                Notification notification = notificationService.createNotification(user.get(), message);
                yield CommandResponse.success("Notification created", notification);
            }
            default -> CommandResponse.error("Unknown notification action: " + action);
        };
    }

    // ==================== LEADERBOARD COMMANDS ====================
    private CommandResponse handleLeaderboardCommand(String action, Command command) {
        Map<String, Object> params = command.getParameters();
        
        return switch (action) {
            case "get" -> {
                List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
                yield CommandResponse.success("Leaderboard retrieved", leaderboard);
            }
            case "gettop" -> {
                Integer n = getIntegerParam(params, "n");
                if (n == null || n <= 0 || n > 100) {
                    yield CommandResponse.error("n must be between 1 and 100");
                }
                List<LeaderboardEntry> topUsers = leaderboardService.getTopN(n);
                yield CommandResponse.success("Top users retrieved", topUsers);
            }
            case "getuserrank" -> {
                Long userId = getLongParam(params, "userId");
                if (userId == null) {
                    yield CommandResponse.error("userId is required");
                }
                LeaderboardEntry entry = leaderboardService.getUserRank(userId);
                if (entry == null) {
                    yield CommandResponse.error("User not found in leaderboard");
                }
                yield CommandResponse.success("User rank retrieved", entry);
            }
            default -> CommandResponse.error("Unknown leaderboard action: " + action);
        };
    }

    // ==================== PROFILE COMMANDS ====================
    private CommandResponse handleProfileCommand(String action, Command command) {
        Map<String, Object> params = command.getParameters();
        
        return switch (action) {
            case "get" -> {
                Long userId = getLongParam(params, "userId");
                if (userId == null) {
                    yield CommandResponse.error("userId is required");
                }
                Optional<User> user = userService.findById(userId);
                if (user.isEmpty()) {
                    yield CommandResponse.error("User not found");
                }
                Map<String, Object> profile = buildProfileResponse(user.get());
                yield CommandResponse.success("Profile retrieved", profile);
            }
            case "update" -> {
                Long userId = getLongParam(params, "userId");
                if (userId == null) {
                    yield CommandResponse.error("userId is required");
                }
                ProfileUpdateRequest request = new ProfileUpdateRequest();
                request.setDisplayName((String) params.get("displayName"));
                request.setBio((String) params.get("bio"));
                request.setProfilePictureUrl((String) params.get("profilePictureUrl"));
                try {
                    UserResponse updated = userService.updateProfile(userId, request);
                    Map<String, Object> profile = buildProfileResponseFromDto(updated);
                    yield CommandResponse.success("Profile updated", profile);
                } catch (RuntimeException e) {
                    yield CommandResponse.error(e.getMessage());
                }
            }
            default -> CommandResponse.error("Unknown profile action: " + action);
        };
    }

    // ==================== HELPER METHODS ====================
    private Sighting createSightingFromParams(Map<String, Object> params) {
        try {
            Sighting sighting = new Sighting();
            sighting.setSpecies((String) params.get("species"));
            sighting.setLocation((String) params.get("location"));
            sighting.setDescription((String) params.get("description"));
            sighting.setImageUrl((String) params.get("imageUrl"));
            
            // Handle user if provided
            Long userId = getLongParam(params, "userId");
            if (userId != null) {
                Optional<User> user = userService.findById(userId);
                user.ifPresent(sighting::setUser);
            }
            
            return sighting;
        } catch (Exception e) {
            return null;
        }
    }

    private Long getLongParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Integer getIntegerParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Map<String, Object> buildProfileResponse(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("bio", user.getBio());
        profile.put("profilePictureUrl", user.getProfilePictureUrl());
        profile.put("totalAnimalsLogged", user.getTotalAnimalsLogged());
        profile.put("uniqueSpeciesCount", user.getUniqueSpeciesCount());
        profile.put("lastActivityDate", user.getLastActivityDate());
        
        // Get rank
        LeaderboardEntry entry = leaderboardService.getUserRank(user.getId());
        profile.put("rank", entry != null ? entry.getRank() : null);
        
        return profile;
    }
    
    private Map<String, Object> buildProfileResponseFromDto(UserResponse userDto) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDto.getId());
        profile.put("username", userDto.getUsername());
        profile.put("displayName", userDto.getDisplayName());
        profile.put("bio", userDto.getBio());
        profile.put("profilePictureUrl", userDto.getProfilePictureUrl());
        profile.put("totalAnimalsLogged", userDto.getTotalAnimalsLogged());
        profile.put("uniqueSpeciesCount", userDto.getUniqueSpeciesCount());
        profile.put("lastActivityDate", userDto.getLastActivityDate());
        
        // Get rank
        LeaderboardEntry entry = leaderboardService.getUserRank(userDto.getId());
        profile.put("rank", entry != null ? entry.getRank() : null);
        
        return profile;
    }
}

