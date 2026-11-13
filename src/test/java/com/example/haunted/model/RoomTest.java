package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.EnumSource;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.ArrayList;

public class RoomTest {
	private Room room;
	private List<Item> loot;
	private List<Monster> monList;
	private Potion healing;
	private Potion strength;
	private Monster monster1;
	private Monster monster2;
	
	@BeforeEach
	void setup() {
		room = new Room("hall", "Abandoned Lecture Hall", "desks");
		healing = new Potion("Healing", "yellow", 10);
		strength = new Potion("Strength", "purple", 5);
		
		
		loot = new ArrayList<>();
		loot.add(healing);
		loot.add(strength);
		
		room.addItem(healing);
		room.addItem(strength);
		
		monster1 = new Monster("Grog", 50, 40, 30, loot);
		monster2 = new Monster("Greg", 50, 40, 30, loot);
		
		monList = new ArrayList<>();
		monList.add(monster1);
		monList.add(monster2);
		
		room.addMonster(monster1);
		room.addMonster(monster2);
	}
	
	//unlocking and key tests
	@ParameterizedTest
    @ValueSource(strings = {"notKey", "ke", "", "KEYS"})
	void wrongKeyDoesntUnlock(String keyTest) {
		room.setLocked(true, "key");
		assertFalse(room.unlock(keyTest));
	}
	
	@ParameterizedTest
    @ValueSource(strings = {"key", "Key", "KEY"})
	void caseDifferencesStillUnlocks(String keyTest) {
		room.setLocked(true, "key");
		assertTrue(room.unlock(keyTest));
	}
	
	@Test
	void unlockingWhenUnlockedReturnsTrue() {
		room.setLocked(false, "key");
		assertTrue(room.unlock("key"));
	}
	
	@Test
	void keySetNullReturnsFalse() {
		room.setLocked(true, null);
		assertFalse(room.unlock("key!"));
	}
	
	//item in room tests
	@Test
	void findItemReturnsItems() {
		assertSame(strength, room.findItem("Strength").orElseThrow());
	}
	
	
	
	//quick getter tests
	@Test
	void getDescriptionReturnsCorrectValue() {
	    assertEquals("desks", room.getDescription());
	}
	
	@Test
	void getExitsIsEmptyAtStart() {
	    assertTrue(room.getExits().isEmpty());
	}

	@ParameterizedTest
	@EnumSource(Direction.class)
	void getExitsReturnsAddedExits(Direction direction) {
	    Room otherRoom = new Room("kitch", "Kitchen", "cooking");
	    room.connect(direction, otherRoom); 

	    assertEquals(otherRoom, room.getExit(direction));
	}
	
	//monster tests
	@Test
	void checkNewMonsterIsAlive() {
		assertTrue(room.hasLivingMonsters());
	}
	
	@Test
	void hasLivingMonsterReturnsCorrect() {
		monster2.takeDamage(100); //kill monster2 first
		assertTrue(room.hasLivingMonsters());
		
		monster1.takeDamage(100);
		assertFalse(room.hasLivingMonsters());
	}
	
	@Test
	void findMonsterReturnsMatchingMonster() {
		//not monster 1, catches "always return true"
		assertSame(monster2, room.findMonster("Greg").orElseThrow());
	}

	
	@Test
	void findMonsterWithNoMonsterReturnsEmpty() {
		assertTrue(room.findMonster("Gron").isEmpty());
	}
	
	@Test
	void getMonsterFindsMonsters() {
		assertFalse(room.getMonsters().isEmpty());
	}
	
	

	
}