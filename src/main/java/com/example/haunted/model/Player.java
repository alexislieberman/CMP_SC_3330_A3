package com.example.haunted.model;

import java.util.Objects;

public class Player {
    private final String name;
    private final int maxHealth;
    private final int baseAttack;
    private final int baseDefense;
    private int health;
    private final Inventory inventory;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private Room currentRoom;

    public Player(String name, int maxHealth, int baseAttack, int baseDefense, Inventory inventory) {
        this.name = Objects.requireNonNull(name);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.inventory = Objects.requireNonNull(inventory);
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

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = Objects.requireNonNull(currentRoom);
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - Math.max(0, amount));
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + Math.max(0, amount));
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getAttackPower() {
        return baseAttack + (equippedWeapon == null ? 0 : equippedWeapon.getAttackBonus());
    }

    public int getDefensePower() {
        return baseDefense + (equippedArmor == null ? 0 : equippedArmor.getDefenseBonus());
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = Objects.requireNonNull(weapon);
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = Objects.requireNonNull(armor);
    }
}
