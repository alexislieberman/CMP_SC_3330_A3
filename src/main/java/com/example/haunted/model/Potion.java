package com.example.haunted.model;

public class Potion extends Item implements Usable {
    private final int healingAmount;

    public Potion(String name, String description, int healingAmount) {
        super(name, description);
        this.healingAmount = healingAmount;
    }

    public int getHealingAmount() {
        return healingAmount;
    }

    @Override
    public void use(Player player) {
        player.heal(healingAmount);
    }
}
