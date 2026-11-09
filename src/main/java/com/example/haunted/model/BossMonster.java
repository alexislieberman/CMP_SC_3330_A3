package com.example.haunted.model;

import java.util.List;

public class BossMonster extends Monster {
    private final int enragedAttackBonus;

    public BossMonster(String name, int health, int attack, int defense, List<Item> loot, int enragedAttackBonus) {
        super(name, health, attack, defense, loot);
        this.enragedAttackBonus = enragedAttackBonus;
    }

    public int getCurrentAttack() {
        if (getHealth() <= getMaxHealth() / 2) {
            return getAttack() + enragedAttackBonus;
        }
        return getAttack();
    }
}
