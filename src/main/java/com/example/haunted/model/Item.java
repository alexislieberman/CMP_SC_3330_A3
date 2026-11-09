package com.example.haunted.model;

import java.util.Objects;

public abstract class Item {
    private final String name;
    private final String description;

    protected Item(String name, String description) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
