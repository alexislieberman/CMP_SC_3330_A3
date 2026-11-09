package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;

public class GameEngine {
    private final Player player;
    private final Quest quest;
    private final MovementEngine movementEngine;
    private final CombatEngine combatEngine;
    private final InteractionEngine interactionEngine;

    public GameEngine(Player player, Quest quest, MovementEngine movementEngine,
                      CombatEngine combatEngine, InteractionEngine interactionEngine) {
        this.player = player;
        this.quest = quest;
        this.movementEngine = movementEngine;
        this.combatEngine = combatEngine;
        this.interactionEngine = interactionEngine;
    }

    public MoveResult move(Direction direction) {
        return movementEngine.move(player, direction);
    }

    public InteractionResult pickUpItem(String itemName) {
        return interactionEngine.pickUpItem(player, quest, itemName);
    }

    public InteractionResult equipItem(String itemName) {
        return interactionEngine.equipItem(player, itemName);
    }

    public InteractionResult useItem(String itemName) {
        return interactionEngine.useItem(player, itemName);
    }

    public InteractionResult unlockRoom(Direction direction) {
        return interactionEngine.unlockRoom(player, direction);
    }

    public CombatResult attack(String monsterName) {
        Monster monster = player.getCurrentRoom().findMonster(monsterName).orElse(null);
        return combatEngine.attack(player, quest, monster);
    }

    public Player getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }

    public Room getCurrentRoom() {
        return player.getCurrentRoom();
    }

    public boolean isGameOver() {
        return !player.isAlive();
    }

    public boolean isGameWon() {
        return quest.isComplete() && player.isAlive();
    }
}
