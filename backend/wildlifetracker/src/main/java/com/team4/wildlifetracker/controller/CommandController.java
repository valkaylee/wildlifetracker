package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.dto.Command;
import com.team4.wildlifetracker.dto.CommandResponse;
import com.team4.wildlifetracker.service.CommandRouter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Central command controller that routes all commands through the CommandRouter.
 * This is the single entry point for all command-based operations.
 * 
 * Note: This does not handle authentication/authorization - those are handled
 * by AuthController. This router focuses on routing commands to existing services.
 */
@RestController
@RequestMapping("/api/command")
@CrossOrigin(origins = "*")
public class CommandController {

    private final CommandRouter commandRouter;

    public CommandController(CommandRouter commandRouter) {
        this.commandRouter = commandRouter;
    }

    /**
     * Execute a command through the central router.
     * 
     * Command format:
     * {
     *   "commandType": "user|sighting|notification|leaderboard|profile",
     *   "action": "create|get|getall|update|delete|...",
     *   "parameters": {
     *     "key1": "value1",
     *     "key2": "value2"
     *   }
     * }
     * 
     * @param command The command to execute
     * @return CommandResponse with the result
     */
    @PostMapping
    public ResponseEntity<CommandResponse> executeCommand(@RequestBody Command command) {
        CommandResponse response = commandRouter.route(command);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get available command types and actions.
     * Useful for API documentation and discovery.
     */
    @GetMapping("/help")
    public ResponseEntity<?> getCommandHelp() {
        return ResponseEntity.ok("""
            Available Command Types and Actions:
            
            USER:
              - get: { "userId": 123 }
            
            SIGHTING:
              - create: { "species": "...", "location": "...", "description": "...", "imageUrl": "...", "userId": 123 }
              - get: { "id": 123 }
              - getall: {}
              - update: { "id": 123, "species": "...", ... }
              - delete: { "id": 123 }
            
            NOTIFICATION:
              - get: { "userId": 123 }
              - create: { "userId": 123, "message": "..." }
              - markread: { "notificationId": 123 }
            
            LEADERBOARD:
              - get: {}
              - gettop: { "n": 10 }
              - getuserrank: { "userId": 123 }
            
            PROFILE:
              - get: { "userId": 123 }
              - update: { "userId": 123, "displayName": "...", "bio": "...", "profilePictureUrl": "..." }
            
            Note: Authentication/Authorization should use /api/auth endpoints.
            """);
    }
}

