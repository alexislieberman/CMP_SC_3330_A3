package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.ArrayList;

public class RoomTest {
	private Room room;
	private Potion healing;
	private Monster monster;
	
	@BeforeEach
	void setup() {
		room = new Room("hall", "Abandoned Lecture Hall", "desks");
		healing = new Potion("Healing", "yellow", 10);
		
		List<Item> loot = new ArrayList<>();
		loot.add(healing);
		monster = new Monster("Grog", 50, 40, 30, loot);
		
		room.addItem(healing);
		room.addMonster(monster);
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
	void keySetNull() {
		room.setLocked(true, null);
		assertFalse(room.unlock("key!"));
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

	@Test
	void getExitsReturnsAddedExits() {
	    Room otherRoom = new Room("kitch", "Kitchen", "cooking");
	    room.connect(Direction.NORTH, otherRoom); 

	    assertEquals(otherRoom, room.getExit(Direction.NORTH));
	}
	
	//monster tests
	@Test
	void checkNewMonsterIsAlive() {
		assertTrue(room.hasLivingMonsters());
	}
	
	@Test
	void makeSureListOfMonstersIsValid() {
		List<Monster> monList = new ArrayList<>();
		monList.add(monster);
		
		assertEquals(monList, room.getMonsters());
	}
	
	

	
}