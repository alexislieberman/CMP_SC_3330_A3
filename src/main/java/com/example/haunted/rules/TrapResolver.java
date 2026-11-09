package com.example.haunted.rules;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;

public class TrapResolver {
    public InteractionResult resolveTrap(Player player, Trap trap) {
        if (trap == null || !trap.isArmed()) {
            return new InteractionResult(false, "No trap was triggered.");
        }

        player.takeDamage(trap.getDamage());
        if (trap.isOneTimeTrigger()) {
            trap.disarm();
        }

        return new InteractionResult(true,
                String.format("Trap '%s' triggered for %d damage.", trap.getName(), trap.getDamage()));
    }
}
