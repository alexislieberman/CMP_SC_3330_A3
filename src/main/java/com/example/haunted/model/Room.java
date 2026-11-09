package com.example.haunted.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Room {
    private final String id;
    private final String name;
    private final String description;
    private final Map<Direction, Room> exits;
    private final List<Item> items;
    private final List<Monster> monsters;
    private boolean locked;
    private String requiredKeyName;
    private Trap trap;

    public Room(String id, String name, String description) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.exits = new EnumMap<>(Direction.class);
        this.items = new ArrayList<>();
        this.monsters = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void connect(Direction direction, Room room) {
        exits.put(Objects.requireNonNull(direction), Objects.requireNonNull(room));
    }

    public Room getExit(Direction direction) {
        return exits.get(direction);
    }

    public Map<Direction, Room> getExits() {
        return Collections.unmodifiableMap(exits);
    }

    public void addItem(Item item) {
        items.add(Objects.requireNonNull(item));
    }

    public Item removeItemByName(String itemName) {
        Optional<Item> item = items.stream().filter(i -> i.getName().equalsIgnoreCase(itemName)).findFirst();
        item.ifPresent(items::remove);
        return item.orElse(null);
    }

    public Optional<Item> findItem(String itemName) {
        return items.stream().filter(i -> i.getName().equalsIgnoreCase(itemName)).findFirst();
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addMonster(Monster monster) {
        monsters.add(Objects.requireNonNull(monster));
    }

    public Optional<Monster> findMonster(String monsterName) {
        return monsters.stream().filter(m -> m.getName().equalsIgnoreCase(monsterName)).findFirst();
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(monsters);
    }

    public boolean hasLivingMonsters() {
        return monsters.stream().anyMatch(Monster::isAlive);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked, String requiredKeyName) {
        this.locked = locked;
        this.requiredKeyName = requiredKeyName;
    }

    public String getRequiredKeyName() {
        return requiredKeyName;
    }

    public boolean unlock(String keyName) {
        if (!locked) {
            return true;
        }
        if (requiredKeyName != null && requiredKeyName.equalsIgnoreCase(keyName)) {
            locked = false;
            return true;
        }
        return false;
    }

    public Trap getTrap() {
        return trap;
    }

    public void setTrap(Trap trap) {
        this.trap = trap;
    }
}
