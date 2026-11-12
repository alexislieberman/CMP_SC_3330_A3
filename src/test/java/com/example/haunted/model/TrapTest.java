package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrapTest {
	private Trap trap;
	
	@BeforeEach
	void setup() {
		trap = new Trap("Fizzler", TrapType.ELECTRIC, 40, false, false);
	}
	
	@Test
	void constructorWorks() {
		assertEquals(TrapType.ELECTRIC, trap.getType());
	}
}
