package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.QuestStatus;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;

class InteractionEngineTest {

    private InteractionEngine engine;
    private Player player;
    private Quest quest;
    private Room room;
    private Room archive;

    // Player in "room"; "archive" is to the NORTH. Inventory holds only 2 items.
    @BeforeEach
    void setUp() {
        engine = new InteractionEngine(new QuestTracker());
        room = new Room("hall", "Abandoned Lecture Hall", "desks");
        archive = new Room("archive", "Exam Archive", "cabinets");
        room.connect(Direction.NORTH, archive);

        player = new Player("Hero", 50, 7, 2, new Inventory(2));
        player.setCurrentRoom(room);
        quest = new Quest("Escape the Basement", "desc");
    }

    // =============== PICK UP ===============

    @ParameterizedTest
    @ValueSource(strings = {"Coffee Potion", "coffee potion", "COFFEE POTION"})
    void pickUpMovesItemFromRoomToInventory(String name) {
        Potion potion = new Potion("Coffee Potion", "warm", 15);
        room.addItem(potion);

        InteractionResult result = engine.pickUpItem(player, quest, name);

        assertTrue(result.isSuccess());
        assertEquals("Picked up Coffee Potion.", result.getMessage());
        assertTrue(room.getItems().isEmpty());
        assertSame(potion, player.getInventory().getItems().get(0));
    }

    @Test
    void pickUpMissingItemFails() {
        InteractionResult result = engine.pickUpItem(player, quest, "Lost Gradebook");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in the room.", result.getMessage());
    }

    @Test
    void pickUpWithFullInventoryLeavesItemInRoom() {
        player.getInventory().addItem(new Key("A", ""));
        player.getInventory().addItem(new Key("B", ""));   // inventory now full (2/2)
        QuestItem gradebook = new QuestItem("Lost Gradebook", "the book");
        room.addItem(gradebook);

        InteractionResult result = engine.pickUpItem(player, quest, "Lost Gradebook");

        assertFalse(result.isSuccess());
        assertEquals("Inventory is full.", result.getMessage());
        assertEquals(List.of(gradebook), room.getItems());    // put back in the room
        assertFalse(quest.isGradebookRecovered());
    }

    @Test
    void pickingUpGradebookAdvancesQuest() {
        room.addItem(new QuestItem("Lost Gradebook", "the book"));

        engine.pickUpItem(player, quest, "Lost Gradebook");

        assertTrue(quest.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    // =============== USE ===============

    @Test
    void usingPotionHealsAndRemovesIt() {
        player.takeDamage(20);                                   // 50 -> 30
        player.getInventory().addItem(new Potion("Coffee Potion", "warm", 15));

        InteractionResult result = engine.useItem(player, "coffee potion");

        assertTrue(result.isSuccess());
        assertEquals("Used Coffee Potion.", result.getMessage());
        assertEquals(45, player.getHealth());                    // 30 + 15
        assertFalse(player.getInventory().contains("Coffee Potion"));
    }

    @Test
    void potionNeverHealsAboveMaxHealth() {
        player.takeDamage(5);                                    // 50 -> 45
        player.getInventory().addItem(new Potion("Coffee Potion", "warm", 15));

        engine.useItem(player, "Coffee Potion");

        assertEquals(50, player.getHealth());                    // capped at max
    }

    @Test
    void usingMissingItemFails() {
        InteractionResult result = engine.useItem(player, "Coffee Potion");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in inventory.", result.getMessage());
    }

    @Test
    void usingNonPotionFailsAndKeepsItem() {
        player.getInventory().addItem(new Key("Archive Key", "opens"));

        InteractionResult result = engine.useItem(player, "Archive Key");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be used.", result.getMessage());
        assertTrue(player.getInventory().contains("Archive Key"));
    }

    // =============== EQUIP ===============

    @Test
    void equippingWeaponRaisesAttack() {
        Weapon stapler = new Weapon("Stapler of Justice", "", 4);
        player.getInventory().addItem(stapler);

        InteractionResult result = engine.equipItem(player, "Stapler of Justice");

        assertTrue(result.isSuccess());
        assertEquals("Equipped weapon Stapler of Justice.", result.getMessage());
        assertSame(stapler, player.getEquippedWeapon());
        assertEquals(11, player.getAttackPower());               // 7 + 4
    }

    @Test
    void equippingArmorRaisesDefense() {
        Armor shield = new Armor("Calculator Shield", "", 3);
        player.getInventory().addItem(shield);

        InteractionResult result = engine.equipItem(player, "Calculator Shield");

        assertTrue(result.isSuccess());
        assertEquals("Equipped armor Calculator Shield.", result.getMessage());
        assertSame(shield, player.getEquippedArmor());
        assertEquals(5, player.getDefensePower());               // 2 + 3
    }

    @Test
    void equippingMissingItemFails() {
        InteractionResult result = engine.equipItem(player, "Stapler of Justice");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in inventory.", result.getMessage());
    }

    @Test
    void equippingNonEquippableItemFails() {
        player.getInventory().addItem(new Potion("Coffee Potion", "", 15));

        InteractionResult result = engine.equipItem(player, "Coffee Potion");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be equipped.", result.getMessage());
        assertNull(player.getEquippedWeapon());
        assertNull(player.getEquippedArmor());
    }

    // =============== LOCKED ROOMS & KEYS ===============

    @ParameterizedTest
    @EnumSource(value = Direction.class, names = {"SOUTH", "EAST", "WEST"})
    void unlockingWhereThereIsNoRoomFails(Direction direction) {
        InteractionResult result = engine.unlockRoom(player, direction);

        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
    }

    @Test
    void unlockingAlreadyUnlockedRoomSucceeds() {
        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("The room is already unlocked.", result.getMessage());
    }

    @Test
    void roomLockedWithNoKeyNameCannotBeUnlocked() {
        archive.setLocked(true, null);

        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertEquals("The room cannot be unlocked.", result.getMessage());
        assertTrue(archive.isLocked());
    }

    @Test
    void unlockingWithoutKeyFails() {
        archive.setLocked(true, "Archive Key");

        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertEquals("You do not have the correct key.", result.getMessage());
        assertTrue(archive.isLocked());
    }

    @Test
    void unlockingWithWrongKeyFails() {
        archive.setLocked(true, "Archive Key");
        player.getInventory().addItem(new Key("Vault Key", ""));

        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertTrue(archive.isLocked());
    }

    @Test
    void itemNamedLikeKeyButNotAKeyDoesNotUnlock() {
        archive.setLocked(true, "Archive Key");
        player.getInventory().addItem(new QuestItem("Archive Key", "just a drawing of a key"));

        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertTrue(archive.isLocked());
    }

    @Test
    void unlockingWithCorrectKeyOpensRoom() {
        archive.setLocked(true, "Archive Key");
        player.getInventory().addItem(new Key("Archive Key", "opens archive"));

        InteractionResult result = engine.unlockRoom(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Unlocked Exam Archive.", result.getMessage());
        assertFalse(archive.isLocked());
        assertTrue(player.getInventory().contains("Archive Key"));   // key is kept
    }
}