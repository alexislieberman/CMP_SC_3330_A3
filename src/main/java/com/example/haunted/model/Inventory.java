package com.example.haunted.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Inventory {
    private final int capacity;
    private final List<Item> items;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (isFull()) {
            return false;
        }
        return items.add(item);
    }

    public Optional<Item> findItem(String itemName) {
        return items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName)).findFirst();
    }

    public Item removeItem(String itemName) {
        Optional<Item> item = findItem(itemName);
        item.ifPresent(items::remove);
        return item.orElse(null);
    }

    public boolean contains(String itemName) {
        return findItem(itemName).isPresent();
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}
