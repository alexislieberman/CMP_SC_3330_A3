package com.example.haunted.config;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.Room;
import com.example.haunted.model.Direction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DungeonFactoryTest {
	 private Map<String, Room> rooms;
	 private GameEngine game;
	    
    @BeforeEach
    void setUp() {
        game = DungeonFactory.createGame();
        rooms = collectRooms(game.getPlayer().getCurrentRoom());
    }
    
    private Map<String, Room> collectRooms(Room start) {
        Map<String, Room> found = new HashMap<>();
        Deque<Room> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty()) {
            Room r = queue.poll();
            if (found.putIfAbsent(r.getId(), r) == null) {
                queue.addAll(r.getExits().values());
            }
        }
        return found;
    }
    
    private static Map<Direction, String> exits(Object... pairs) {
        Map<Direction, String> m = new EnumMap<>(Direction.class);
        for (int i = 0; i < pairs.length; i += 2) {
            m.put((Direction) pairs[i], (String) pairs[i + 1]);
        }
        return m;
    }
    

    @Test
    void constructorIsPrivate_andCanBeInvoked() throws Exception {
        Constructor<DungeonFactory> constructor = DungeonFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
    
    @Test
    void connectionsAreWiredAsCoded() {
        Map<String, Map<Direction, String>> expected = Map.of(
            "stairwell", exits(Direction.EAST, "lectureHall"),
            "lectureHall", exits(Direction.WEST, "stairwell", Direction.EAST, "labStorage",
                                 Direction.SOUTH, "brokenElevator", Direction.NORTH, "examArchive"),
            "labStorage", exits(Direction.WEST, "lectureHall", Direction.NORTH, "serverCloset"),
            "brokenElevator", exits(Direction.NORTH, "lectureHall"),
            "serverCloset", exits(Direction.SOUTH, "labStorage", Direction.EAST, "deanVault"),
            "examArchive", exits(Direction.SOUTH, "lectureHall", Direction.EAST, "deanVault"),
            "deanVault", exits(Direction.WEST, "examArchive", Direction.SOUTH, "serverCloset",
                               Direction.NORTH, "finalChamber"),
            "finalChamber", exits(Direction.SOUTH, "deanVault")
        );

        expected.forEach((fromId, edges) -> {
            Room from = rooms.get(fromId);
            assertEquals(edges.size(), from.getExits().size());
            edges.forEach((dir, toId) -> assertSame(rooms.get(toId), from.getExit(dir)));
        });
    }
    
    @Test
    void locksAreSet() {
        assertTrue(rooms.get("examArchive").isLocked());
        assertEquals("Archive Key", rooms.get("examArchive").getRequiredKeyName());
        assertTrue(rooms.get("finalChamber").isLocked());
        assertEquals("Vault Key", rooms.get("finalChamber").getRequiredKeyName());

        // every other room is unlocked
        rooms.forEach((id, room) -> {
            if (!id.equals("examArchive") && !id.equals("finalChamber")) {
                assertFalse(room.isLocked());
            }
        });
    }
    
}
