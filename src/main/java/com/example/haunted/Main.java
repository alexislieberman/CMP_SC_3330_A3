package com.example.haunted;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.Direction;

public class Main {
    public static void main(String[] args) {
        GameEngine game = DungeonFactory.createGame();
        System.out.println(game.getCurrentRoom().getName());
        System.out.println(game.move(Direction.EAST).getMessage());
        System.out.println(game.pickUpItem("Coffee Potion").getMessage());
    }
}
