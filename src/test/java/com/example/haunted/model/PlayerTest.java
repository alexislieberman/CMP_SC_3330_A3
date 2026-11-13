package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
	private Player player;
	private Inventory inventory;
	private Potion healing;
	
	@BeforeEach
	void setup() {
		inventory = new Inventory(2);
		player = new Player("joe", 50, 10, 80, inventory);
		healing = new Potion("Healing", "yellow", 10);
	}
	
	@Test
	void playerConstructorIsCorrect() {
		assertEquals("joe", player.getName());
		assertEquals(50, player.getMaxHealth());
		assertEquals(10, player.getBaseAttack());
		assertEquals(80, player.getBaseDefense());
	}
	
	
	//some inventory tests
	@Test
	void inventoryConstructorIsCorrect() {
		assertEquals(2, inventory.getCapacity());
	}
	
	@Test
	void addingItemReturnsCorrect() {
		assertTrue(inventory.addItem(healing));
		assertSame(healing, inventory.findItem("healing").orElseThrow()); //check that item actually got added
		
		//impossible to catch always true mutation
	}


	@Test
	void removeItemReturnsAndRemovesMatchingItem() {
		inventory.addItem(healing);
		assertEquals(healing, inventory.removeItem("Healing"));
		assertNull(inventory.removeItem("Healing"));   // already removed, should be null
	}
}
