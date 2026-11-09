package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestStatus;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;

class CombatEngineTest {

    private CombatEngine engine;
    private Player player;
    private Quest quest;
    private Room room;

    @BeforeEach
    void setUp() {
        engine = new CombatEngine(new DamageCalculator(), new QuestTracker());
        room = new Room("hall", "Abandoned Lecture Hall", "desks");
        player = new Player("Hero", 50, 7, 2, new Inventory(8));   // 50 hp, 7 attack, 2 defense
        player.setCurrentRoom(room);
        quest = new Quest("Escape the Basement", "desc");
    }

    @Test
    void attackingMissingMonsterFails() {
        CombatResult result = engine.attack(player, quest, null);

        assertFalse(result.isSuccess());
        assertEquals("Monster not found.", result.getMessage());
        assertEquals(0, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
    }

    @Test
    void deadPlayerCannotAttack() {
        Monster ghost = new Monster("Ghost", 20, 6, 1, List.of());
        player.takeDamage(50);

        CombatResult result = engine.attack(player, quest, ghost);

        assertFalse(result.isSuccess());
        assertEquals("Player is defeated.", result.getMessage());
        assertEquals(20, ghost.getHealth());   // monster not hurt
    }

    @Test
    void attackingAlreadyDefeatedMonsterFails() {
        Monster ghost = new Monster("Ghost", 20, 6, 1, List.of(new Potion("Coffee Potion", "", 15)));
        ghost.takeDamage(20);

        CombatResult result = engine.attack(player, quest, ghost);

        assertFalse(result.isSuccess());
        assertEquals("Monster is already defeated.", result.getMessage());
        assertTrue(result.isMonsterDefeated());
        assertTrue(room.getItems().isEmpty());   // loot not dropped again
    }

    @Test
    void hitThatDoesNotKillGetsCounterAttacked() {
        Monster ta = new Monster("Sleep-Deprived TA", 18, 6, 1, List.of(new Potion("Coffee Potion", "", 15)));

        CombatResult result = engine.attack(player, quest, ta);

        assertTrue(result.isSuccess());
        assertEquals("Attacked Sleep-Deprived TA.", result.getMessage());
        assertEquals(6, result.getDamageToMonster());   // 7 - 1
        assertEquals(4, result.getDamageToPlayer());    // 6 - 2
        assertFalse(result.isMonsterDefeated());
        assertEquals(12, ta.getHealth());
        assertEquals(46, player.getHealth());
        assertTrue(room.getItems().isEmpty());          // no loot yet
    }

    @Test
    void killingBlowDropsLootAndNoCounterAttack() {
        Potion potion = new Potion("Coffee Potion", "", 15);
        Weapon sword = new Weapon("Dry Erase Sword", "", 6);
        Monster ta = new Monster("Sleep-Deprived TA", 6, 6, 1, List.of(potion, sword));

        CombatResult result = engine.attack(player, quest, ta);

        assertTrue(result.isSuccess());
        assertEquals("Defeated Sleep-Deprived TA.", result.getMessage());
        assertEquals(0, result.getDamageToPlayer());    // dead monsters don't hit back
        assertTrue(result.isMonsterDefeated());
        assertEquals(List.of(potion, sword), result.getDroppedItems());
        assertEquals(List.of(potion, sword), room.getItems());   // loot is in the room
        assertEquals(50, player.getHealth());
    }

    // monster health, defeated?  (player always deals 6 damage)
    @ParameterizedTest
    @CsvSource({
        "5, true",
        "6, true",    // boundary: damage exactly equals health
        "7, false"    // boundary: 1 health left
    })
    void killBoundary(int monsterHealth, boolean expectedDefeated) {
        Monster monster = new Monster("Ghost", monsterHealth, 6, 1, List.of());

        CombatResult result = engine.attack(player, quest, monster);

        assertEquals(expectedDefeated, result.isMonsterDefeated());
    }

    @Test
    void defeatingFinalExamPhantomUpdatesQuest() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 3, 10, 4, List.of(), 3);
        player.equipWeapon(new Weapon("Stapler", "", 4));   // 11 - 4 = 7 damage

        engine.attack(player, quest, phantom);

        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @Test
    void woundingPhantomDoesNotUpdateQuest() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);

        engine.attack(player, quest, phantom);

        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void enragedBossHitsHarder() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);

        CombatResult first = engine.attack(player, quest, phantom);    // 40 -> 37, normal
        assertEquals(8, first.getDamageToPlayer());                      // 10 - 2

        phantom.takeDamage(15);                                          // 37 -> 22
        CombatResult second = engine.attack(player, quest, phantom);   // 22 -> 19, enraged
        assertEquals(11, second.getDamageToPlayer());                    // (10 + 3) - 2
    }
}