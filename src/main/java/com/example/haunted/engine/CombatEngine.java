package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Quest;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;

import java.util.ArrayList;
import java.util.List;

public class CombatEngine {
    private final DamageCalculator damageCalculator;
    private final QuestTracker questTracker;

    public CombatEngine(DamageCalculator damageCalculator, QuestTracker questTracker) {
        this.damageCalculator = damageCalculator;
        this.questTracker = questTracker;
    }

    public CombatResult attack(Player player, Quest quest, Monster monster) {
        if (monster == null) {
            return new CombatResult(false, "Monster not found.", 0, 0, false, List.of());
        }
        if (!player.isAlive()) {
            return new CombatResult(false, "Player is defeated.", 0, 0, false, List.of());
        }
        if (!monster.isAlive()) {
            return new CombatResult(false, "Monster is already defeated.", 0, 0, true, List.of());
        }

        int damageToMonster = damageCalculator.calculatePlayerDamage(player, monster);
        monster.takeDamage(damageToMonster);

        int damageToPlayer = 0;
        boolean defeated = !monster.isAlive();
        List<Item> droppedItems = new ArrayList<>();

        if (defeated) {
            Room room = player.getCurrentRoom();
            for (Item item : monster.getLoot()) {
                room.addItem(item);
                droppedItems.add(item);
            }
            questTracker.updateQuestForMonster(quest, monster);
            return new CombatResult(true,
                    "Defeated " + monster.getName() + ".",
                    damageToMonster,
                    0,
                    true,
                    droppedItems);
        }

        damageToPlayer = damageCalculator.calculateMonsterDamage(monster, player);
        player.takeDamage(damageToPlayer);
        return new CombatResult(true,
                "Attacked " + monster.getName() + ".",
                damageToMonster,
                damageToPlayer,
                false,
                droppedItems);
    }
}
