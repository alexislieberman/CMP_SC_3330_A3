package com.example.haunted.events;

public class MoveResult {
    private final boolean success;
    private final String message;
    private final boolean trapTriggered;
    private final int trapDamage;

    public MoveResult(boolean success, String message, boolean trapTriggered, int trapDamage) {
        this.success = success;
        this.message = message;
        this.trapTriggered = trapTriggered;
        this.trapDamage = trapDamage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isTrapTriggered() {
        return trapTriggered;
    }

    public int getTrapDamage() {
        return trapDamage;
    }
}
