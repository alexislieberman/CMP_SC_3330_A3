package com.example.haunted.model;

import java.util.Objects;

public class Trap {
    private final String name;
    private final TrapType type;
    private final int damage;
    private final boolean oneTimeTrigger;
    private boolean armed;

    public Trap(String name, TrapType type, int damage, boolean armed, boolean oneTimeTrigger) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.damage = damage;
        this.armed = armed;
        this.oneTimeTrigger = oneTimeTrigger;
    }

    public String getName() {
        return name;
    }

    public TrapType getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isOneTimeTrigger() {
        return oneTimeTrigger;
    }

    public void disarm() {
        this.armed = false;
    }
}
