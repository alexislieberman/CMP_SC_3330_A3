package com.example.haunted.engine;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.rules.TrapResolver;

public class MovementEngine {
    private final TrapResolver trapResolver;

    public MovementEngine(TrapResolver trapResolver) {
        this.trapResolver = trapResolver;
    }

    public MoveResult move(Player player, Direction direction) {
        Room currentRoom = player.getCurrentRoom();
        Room targetRoom = currentRoom.getExit(direction);

        if (targetRoom == null) {
            return new MoveResult(false, "There is no room in that direction.", false, 0);
        }

        if (targetRoom.isLocked()) {
            return new MoveResult(false, "The room is locked.", false, 0);
        }

        player.setCurrentRoom(targetRoom);
        Trap trap = targetRoom.getTrap();
        if (trap != null && trap.isArmed()) {
            InteractionResult trapResult = trapResolver.resolveTrap(player, trap);
            return new MoveResult(true,
                    "Moved to " + targetRoom.getName() + ". " + trapResult.getMessage(),
                    true,
                    trap.getDamage());
        }

        return new MoveResult(true, "Moved to " + targetRoom.getName() + ".", false, 0);
    }
}
