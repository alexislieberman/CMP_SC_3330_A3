package com.example.haunted.events;

public class InteractionResult {
    private final boolean success;
    private final String message;

    public InteractionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
