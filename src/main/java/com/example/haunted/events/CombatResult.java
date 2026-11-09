package com.example.haunted.events;

import com.example.haunted.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombatResult {
    private final boolean success;
    private final String message;
    private final int damageToMonster;
    private final int damageToPlayer;
    private final boolean monsterDefeated;
    private final List<Item> droppedItems;

    public CombatResult(boolean success, String message, int damageToMonster, int damageToPlayer,
                        boolean monsterDefeated, List<Item> droppedItems) {
        this.success = success;
        this.message = message;
        this.damageToMonster = damageToMonster;
        this.damageToPlayer = damageToPlayer;
        this.monsterDefeated = monsterDefeated;
        this.droppedItems = new ArrayList<>(droppedItems);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getDamageToMonster() {
        return damageToMonster;
    }

    public int getDamageToPlayer() {
        return damageToPlayer;
    }

    public boolean isMonsterDefeated() {
        return monsterDefeated;
    }

    public List<Item> getDroppedItems() {
        return Collections.unmodifiableList(droppedItems);
    }
}
