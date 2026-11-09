package com.example.haunted.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Monster {
    private final String name;
    private final int maxHealth;
    private int health;
    private final int attack;
    private final int defense;
    private final List<Item> loot;

    public Monster(String name, int health, int attack, int defense, List<Item> loot) {
        this.name = Objects.requireNonNull(name);
        this.maxHealth = health;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.loot = new ArrayList<>(Objects.requireNonNull(loot));
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - Math.max(0, amount));
    }

    public boolean isAlive() {
        return health > 0;
    }

    public List<Item> getLoot() {
        return Collections.unmodifiableList(loot);
    }
}
