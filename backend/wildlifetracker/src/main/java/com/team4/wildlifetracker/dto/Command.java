package com.team4.wildlifetracker.dto;

import java.util.Map;

/**
 * Command DTO for the central router system.
 * Routes commands to appropriate service handlers.
 */
public class Command {
    private String commandType;
    private String action;
    private Map<String, Object> parameters;
    private Long userId; // Optional: for authenticated commands

    public Command() {}

    public Command(String commandType, String action, Map<String, Object> parameters) {
        this.commandType = commandType;
        this.action = action;
        this.parameters = parameters;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

