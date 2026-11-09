package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.QuestStatus;

/** Tests GameEngine using the real dungeon from DungeonFactory. */
class GameEngineTest {

    private GameEngine game;

    @BeforeEach
    void setUp() {
        game = DungeonFactory.createGame();   // fresh game for every test
    }

    @Test
    void gameStartsInStairwell() {
        assertEquals("Maintenance Stairwell", game.getCurrentRoom().getName());
        assertSame(game.getPlayer().getCurrentRoom(), game.getCurrentRoom());
    }

    @Test
    void newGameIsNeitherOverNorWon() {
        assertFalse(game.isGameOver());
        assertFalse(game.isGameWon());
    }

    // =============== delegation to the engines ===============

    @Test
    void moveChangesRoom() {
        MoveResult result = game.move(Direction.EAST);

        assertTrue(result.isSuccess());
        assertEquals("Moved to Abandoned Lecture Hall.", result.getMessage());
        assertEquals("lectureHall", game.getCurrentRoom().getId());
    }

    @Test
    void moveIntoWallFails() {
        assertFalse(game.move(Direction.WEST).isSuccess());
        assertEquals("stairwell", game.getCurrentRoom().getId());
    }

    @Test
    void pickUpEquipAndUseWorkThroughGame() {
        game.move(Direction.EAST);
        assertTrue(game.pickUpItem("Coffee Potion").isSuccess());

        game.move(Direction.EAST);
        assertTrue(game.pickUpItem("Calculator Shield").isSuccess());
        assertTrue(game.equipItem("Calculator Shield").isSuccess());
        assertEquals(5, game.getPlayer().getDefensePower());

        game.getPlayer().takeDamage(20);
        assertTrue(game.useItem("Coffee Potion").isSuccess());
        assertEquals(45, game.getPlayer().getHealth());
    }

    @Test
    void interactionFailuresAreReported() {
        assertEquals("Item not found in the room.", game.pickUpItem("Lost Gradebook").getMessage());
        assertEquals("Item not found in inventory.", game.equipItem("Stapler of Justice").getMessage());
        assertEquals("Item not found in inventory.", game.useItem("Coffee Potion").getMessage());
    }

    @Test
    void unlockArchiveWithKeyFromLabStorage() {
        game.move(Direction.EAST);
        InteractionResult noKey = game.unlockRoom(Direction.NORTH);
        assertFalse(noKey.isSuccess());

        game.move(Direction.EAST);
        game.pickUpItem("Archive Key");
        game.move(Direction.WEST);

        InteractionResult unlocked = game.unlockRoom(Direction.NORTH);
        assertTrue(unlocked.isSuccess());
        assertEquals("Unlocked Exam Archive.", unlocked.getMessage());
        assertTrue(game.move(Direction.NORTH).isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sleep-Deprived TA", "sleep-deprived ta"})
    void attackFindsMonsterIgnoringCase(String name) {
        game.move(Direction.EAST);

        CombatResult result = game.attack(name);

        assertTrue(result.isSuccess());
        assertEquals(6, result.getDamageToMonster());
        assertEquals(4, result.getDamageToPlayer());
    }

    @Test
    void attackingMonsterNotInRoomFails() {
        CombatResult result = game.attack("Sleep-Deprived TA");   // still in stairwell

        assertFalse(result.isSuccess());
        assertEquals("Monster not found.", result.getMessage());
    }

    @Test
    void brokenElevatorTrapTriggersOnlyOnce() {
        game.move(Direction.EAST);

        MoveResult first = game.move(Direction.SOUTH);
        assertTrue(first.isTrapTriggered());
        assertEquals(42, game.getPlayer().getHealth());

        game.move(Direction.NORTH);
        MoveResult second = game.move(Direction.SOUTH);
        assertFalse(second.isTrapTriggered());
        assertEquals(42, game.getPlayer().getHealth());
    }

    // =============== game over / game won ===============

    @Test
    void gameIsOverWhenPlayerDies() {
        game.getPlayer().takeDamage(49);
        assertFalse(game.isGameOver());     // 1 health left

        game.getPlayer().takeDamage(1);
        assertTrue(game.isGameOver());      // 0 health
    }

    // gradebook found?, phantom beaten?, damage taken, won?
    @ParameterizedTest
    @CsvSource({
        "false, false, 0,  false",
        "true,  false, 0,  false",
        "false, true,  0,  false",
        "true,  true,  0,  true",
        "true,  true,  49, true",     // alive with 1 health -> still wins
        "true,  true,  50, false"     // quest done but dead -> no win
    })
    void gameIsWonOnlyWhenQuestCompleteAndPlayerAlive(boolean gradebook, boolean phantom,
                                                      int damage, boolean expectedWon) {
        if (gradebook) {
            game.getQuest().markGradebookRecovered();
        }
        if (phantom) {
            game.getQuest().markPhantomDefeated();
        }
        game.getPlayer().takeDamage(damage);

        assertEquals(expectedWon, game.isGameWon());
    }

    // =============== full playthrough ===============

    @Test
    void fullPlaythroughWinsTheGame() {
        // Lecture Hall: grab coffee, beat the TA
        assertTrue(game.move(Direction.EAST).isSuccess());
        assertTrue(game.pickUpItem("Coffee Potion").isSuccess());
        attackUntilDefeated("Sleep-Deprived TA", 3);
        assertEquals(42, game.getPlayer().getHealth());
        assertFalse(game.move(Direction.NORTH).isSuccess(), "archive is locked");

        // Lab Storage: key + shield
        assertTrue(game.move(Direction.EAST).isSuccess());
        assertTrue(game.pickUpItem("Archive Key").isSuccess());
        assertTrue(game.pickUpItem("Calculator Shield").isSuccess());
        assertTrue(game.equipItem("Calculator Shield").isSuccess());

        // Server Closet: stapler, beat golem, take its sword
        assertTrue(game.move(Direction.NORTH).isSuccess());
        assertTrue(game.pickUpItem("Stapler of Justice").isSuccess());
        assertTrue(game.equipItem("Stapler of Justice").isSuccess());
        attackUntilDefeated("Spreadsheet Golem", 4);
        assertEquals(36, game.getPlayer().getHealth());
        assertTrue(game.pickUpItem("Dry Erase Sword").isSuccess());
        assertTrue(game.equipItem("Dry Erase Sword").isSuccess());

        // Exam Archive: unlock, beat ghost, get gradebook
        assertTrue(game.move(Direction.SOUTH).isSuccess());
        assertTrue(game.move(Direction.WEST).isSuccess());
        assertTrue(game.unlockRoom(Direction.NORTH).isSuccess());
        assertTrue(game.move(Direction.NORTH).isSuccess());
        attackUntilDefeated("Plagiarism Ghost", 2);
        assertTrue(game.pickUpItem("Lost Gradebook").isSuccess());
        assertEquals(QuestStatus.IN_PROGRESS, game.getQuest().getStatus());

        // Dean Vault: beat wraith, take vault key and gown
        assertTrue(game.move(Direction.EAST).isSuccess());
        attackUntilDefeated("Registrar Wraith", 3);
        assertTrue(game.pickUpItem("Vault Key").isSuccess());
        assertTrue(game.pickUpItem("Graduation Gown Armor").isSuccess());
        assertTrue(game.equipItem("Graduation Gown Armor").isSuccess());

        // Final Chamber: beat the boss
        assertTrue(game.unlockRoom(Direction.NORTH).isSuccess());
        assertTrue(game.move(Direction.NORTH).isSuccess());
        assertFalse(game.isGameWon());
        attackUntilDefeated("Final Exam Phantom", 5);

        assertEquals(7, game.getPlayer().getHealth());
        assertEquals(QuestStatus.COMPLETED, game.getQuest().getStatus());
        assertTrue(game.isGameWon());
        assertFalse(game.isGameOver());
    }

    // Helper: attacks until the monster falls, checking it takes exactly the expected number of hits
    private void attackUntilDefeated(String monsterName, int expectedHits) {
        for (int hit = 1; hit < expectedHits; hit++) {
            assertFalse(game.attack(monsterName).isMonsterDefeated(),
                    monsterName + " should survive hit " + hit);
        }
        assertTrue(game.attack(monsterName).isMonsterDefeated(),
                monsterName + " should fall on hit " + expectedHits);
    }
}