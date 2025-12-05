package com.team4.wildlifetracker.dto;

/**
 * Response wrapper for command execution results.
 */
public class CommandResponse {
    private boolean success;
    private String message;
    private Object data;
    private String error;

    public CommandResponse() {}

    public CommandResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static CommandResponse success(String message, Object data) {
        return new CommandResponse(true, message, data);
    }

    public static CommandResponse success(Object data) {
        return new CommandResponse(true, "Command executed successfully", data);
    }

    public static CommandResponse error(String error) {
        CommandResponse response = new CommandResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

