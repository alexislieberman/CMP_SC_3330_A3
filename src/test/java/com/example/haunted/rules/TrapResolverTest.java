package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

class TrapResolverTest {

    private TrapResolver resolver;
    private Player player;

    @BeforeEach
    void setUp() {
        resolver = new TrapResolver();
        player = new Player("Hero", 50, 7, 2, new Inventory(5));   // starts at 50 health
    }

    @Test
    void noTrapDoesNothing() {
        InteractionResult result = resolver.resolveTrap(player, null);

        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        assertEquals(50, player.getHealth());
    }

    @Test
    void disarmedTrapDoesNothing() {
        Trap trap = new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, false, true);  // armed = false

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        assertEquals(50, player.getHealth());
    }

    // trap name, type, damage, player health afterwards
    @ParameterizedTest
    @CsvSource({
        "Loose Wires Trap, ELECTRIC, 8, 42",
        "Steam Vent, STEAM, 1, 49",
        "Big Zap, ELECTRIC, 50, 0",     // boundary: exactly lethal
        "Overkill, STEAM, 75, 0"        // health never goes below 0
    })
    void armedTrapDamagesPlayer(String name, TrapType type, int damage, int expectedHealth) {
        Trap trap = new Trap(name, type, damage, true, false);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals("Trap '" + name + "' triggered for " + damage + " damage.", result.getMessage());
        assertEquals(expectedHealth, player.getHealth());
    }

    @Test
    void oneTimeTrapDisarmsItselfAfterTriggering() {
        Trap trap = new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true);  // one-time = true

        assertTrue(resolver.resolveTrap(player, trap).isSuccess());
        assertFalse(trap.isArmed());

        assertFalse(resolver.resolveTrap(player, trap).isSuccess());  // second time: nothing
        assertEquals(42, player.getHealth());                          // only hurt once
    }

    @Test
    void reusableTrapTriggersEveryTime() {
        Trap trap = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);  // one-time = false

        resolver.resolveTrap(player, trap);
        resolver.resolveTrap(player, trap);

        assertTrue(trap.isArmed());
        assertEquals(40, player.getHealth());   // hurt twice: 50 - 5 - 5
    }
}