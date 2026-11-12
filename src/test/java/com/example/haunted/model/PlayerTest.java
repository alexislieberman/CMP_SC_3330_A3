package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
	private Player player;
	private Inventory inventory;
	
	@BeforeEach
	void setup() {
		inventory = new Inventory(50);
		player = new Player("joe", 50, 10, 80, inventory);
	}
	
	@Test
	void playerConstructorIsCorrect() {
		assertEquals("joe", player.getName());
		assertEquals(50, player.getMaxHealth());
		assertEquals(10, player.getBaseAttack());
		assertEquals(80, player.getBaseDefense());
	}
	
	@Test
	void inventoryConstructorIsCorrect() {
		assertEquals(50, inventory.getCapacity());
	}
	
}
