package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuestTest {
	private Quest quest;
	
	@BeforeEach
	void setup() {
		quest = new Quest("Mission", "bySea");
	}
	
	@Test
	void constructorIsCorrect() {
		assertEquals("Mission", quest.getName());
		assertEquals("bySea", quest.getDescription());
	}
}
