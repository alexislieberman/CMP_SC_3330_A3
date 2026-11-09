package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;

class MovementEngineTest {

    private MovementEngine engine;
    private Player player;
    private Room start;
    private Room east;

    // Two connected rooms: start <-> east
    @BeforeEach
    void setUp() {
        engine = new MovementEngine(new TrapResolver());
        start = new Room("start", "Maintenance Stairwell", "start room");
        east = new Room("east", "Abandoned Lecture Hall", "east room");
        start.connect(Direction.EAST, east);
        east.connect(Direction.WEST, start);

        player = new Player("Hero", 50, 7, 2, new Inventory(8));
        player.setCurrentRoom(start);
    }

    @Test
    void movingThroughOpenExitChangesRoom() {
        MoveResult result = engine.move(player, Direction.EAST);

        assertTrue(result.isSuccess());
        assertEquals("Moved to Abandoned Lecture Hall.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
        assertSame(east, player.getCurrentRoom());
    }

    // Runs once for each direction that has NO exit
    @ParameterizedTest
    @EnumSource(value = Direction.class, names = {"NORTH", "SOUTH", "WEST"})
    void movingWhereThereIsNoExitFails(Direction direction) {
        MoveResult result = engine.move(player, direction);

        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
        assertSame(start, player.getCurrentRoom());   // player didn't move
    }

    @Test
    void movingIntoLockedRoomFails() {
        east.setLocked(true, "Archive Key");

        MoveResult result = engine.move(player, Direction.EAST);

        assertFalse(result.isSuccess());
        assertEquals("The room is locked.", result.getMessage());
        assertSame(start, player.getCurrentRoom());
    }

    @Test
    void movingIntoRoomAfterUnlockingSucceeds() {
        east.setLocked(true, "Archive Key");
        east.unlock("Archive Key");

        assertTrue(engine.move(player, Direction.EAST).isSuccess());
        assertSame(east, player.getCurrentRoom());
    }

    @Test
    void enteringRoomWithArmedTrapTriggersIt() {
        east.setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true));

        MoveResult result = engine.move(player, Direction.EAST);

        assertTrue(result.isSuccess());
        assertEquals("Moved to Abandoned Lecture Hall. Trap 'Loose Wires Trap' triggered for 8 damage.",
                result.getMessage());
        assertTrue(result.isTrapTriggered());
        assertEquals(8, result.getTrapDamage());
        assertEquals(42, player.getHealth());
    }

    @Test
    void disarmedTrapDoesNotTrigger() {
        east.setTrap(new Trap("Steam Vent", TrapType.STEAM, 5, false, false));   // not armed

        MoveResult result = engine.move(player, Direction.EAST);

        assertTrue(result.isSuccess());
        assertEquals("Moved to Abandoned Lecture Hall.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        assertEquals(50, player.getHealth());
    }

    @Test
    void oneTimeTrapOnlyTriggersOnFirstEntry() {
        east.setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true));

        engine.move(player, Direction.EAST);   // triggers
        engine.move(player, Direction.WEST);
        MoveResult second = engine.move(player, Direction.EAST);   // already disarmed

        assertFalse(second.isTrapTriggered());
        assertEquals(42, player.getHealth());
    }

    @Test
    void reusableTrapTriggersEveryEntry() {
        east.setTrap(new Trap("Steam Vent", TrapType.STEAM, 5, true, false));

        engine.move(player, Direction.EAST);
        engine.move(player, Direction.WEST);
        MoveResult second = engine.move(player, Direction.EAST);

        assertTrue(second.isTrapTriggered());
        assertEquals(5, second.getTrapDamage());
        assertEquals(40, player.getHealth());
    }
}