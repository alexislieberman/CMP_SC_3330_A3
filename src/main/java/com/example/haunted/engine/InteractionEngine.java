package com.example.haunted.engine;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.model.Quest;

import java.util.Optional;

public class InteractionEngine {
    private final QuestTracker questTracker;

    public InteractionEngine(QuestTracker questTracker) {
        this.questTracker = questTracker;
    }

    public InteractionResult pickUpItem(Player player, Quest quest, String itemName) {
        Room room = player.getCurrentRoom();
        Item item = room.removeItemByName(itemName);
        if (item == null) {
            return new InteractionResult(false, "Item not found in the room.");
        }
        if (!player.getInventory().addItem(item)) {
            room.addItem(item);
            return new InteractionResult(false, "Inventory is full.");
        }
        questTracker.updateQuestForItem(quest, item);
        return new InteractionResult(true, "Picked up " + item.getName() + ".");
    }

    public InteractionResult useItem(Player player, String itemName) {
        Optional<Item> itemOptional = player.getInventory().findItem(itemName);
        if (itemOptional.isEmpty()) {
            return new InteractionResult(false, "Item not found in inventory.");
        }

        Item item = itemOptional.get();
        if (!(item instanceof Potion potion)) {
            return new InteractionResult(false, "That item cannot be used.");
        }

        potion.use(player);
        player.getInventory().removeItem(itemName);
        return new InteractionResult(true, "Used " + item.getName() + ".");
    }

    public InteractionResult equipItem(Player player, String itemName) {
        Optional<Item> itemOptional = player.getInventory().findItem(itemName);
        if (itemOptional.isEmpty()) {
            return new InteractionResult(false, "Item not found in inventory.");
        }

        Item item = itemOptional.get();
        if (item instanceof Weapon weapon) {
            player.equipWeapon(weapon);
            return new InteractionResult(true, "Equipped weapon " + item.getName() + ".");
        }
        if (item instanceof Armor armor) {
            player.equipArmor(armor);
            return new InteractionResult(true, "Equipped armor " + item.getName() + ".");
        }
        return new InteractionResult(false, "That item cannot be equipped.");
    }

    public InteractionResult unlockRoom(Player player, com.example.haunted.model.Direction direction) {
        Room currentRoom = player.getCurrentRoom();
        Room targetRoom = currentRoom.getExit(direction);
        if (targetRoom == null) {
            return new InteractionResult(false, "There is no room in that direction.");
        }
        if (!targetRoom.isLocked()) {
            return new InteractionResult(true, "The room is already unlocked.");
        }
        String requiredKeyName = targetRoom.getRequiredKeyName();
        if (requiredKeyName == null) {
            return new InteractionResult(false, "The room cannot be unlocked.");
        }
        Optional<Item> key = player.getInventory().findItem(requiredKeyName);
        if (key.isEmpty() || !(key.get() instanceof Key)) {
            return new InteractionResult(false, "You do not have the correct key.");
        }
        targetRoom.unlock(requiredKeyName);
        return new InteractionResult(true, "Unlocked " + targetRoom.getName() + ".");
    }
}
