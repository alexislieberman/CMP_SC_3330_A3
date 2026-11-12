package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemTest {
	private Potion healing;
	private Room room;
	
	@BeforeEach
	void setup() {
		healing = new Potion("Healing", "yellow", 10);
		room = new Room("kitch", "Kitchen", "cooking");
	}
	
	//item (potion) tests
	@Test
	void getDescriptionReturnsCorrect() {
	    assertEquals("yellow", healing.getDescription());
	}
	
	@Test
	void toStringReturnsCorrect() {
	    assertEquals("Healing", healing.toString());
	}
	
	@Test
	void HealingAmountCorrect() {
	    assertEquals(10, healing.getHealingAmount());
	}
	
	@Test
	void missingItemReturnsEmpty() {
		room.addItem(healing);
		assertTrue(room.findItem("sword").isEmpty());
	}
}
