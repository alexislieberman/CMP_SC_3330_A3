package com.example.haunted.model;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;


public class RoomTest {
	private Room room;
	private Weapon sword;
	
	@BeforeEach
	void setup() {
		room = new Room("hall", "Abandoned Lecture Hall", "desks");
		sword = new Weapon("sword", "sharp", 10);
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
	
	
	//item (weapon) tests
	@Test
	void getDescriptionReturnsCorrect() {
	    assertEquals("sharp", sword.getDescription());
	}
	
	@Test
	void toStringReturnsCorrect() {
	    assertEquals("sword", sword.toString());
	}
}