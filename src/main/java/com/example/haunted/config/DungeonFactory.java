package com.example.haunted.config;

import com.example.haunted.engine.CombatEngine;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.engine.InteractionEngine;
import com.example.haunted.engine.MovementEngine;
import com.example.haunted.model.Armor;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;

import java.util.List;

public final class DungeonFactory {
    private DungeonFactory() {
    }

    public static GameEngine createGame() {
        Room stairwell = new Room("stairwell", "Maintenance Stairwell", "The flickering stairwell leading into the basement.");
        Room lectureHall = new Room("lectureHall", "Abandoned Lecture Hall", "Dusty desks and a broken projector.");
        Room labStorage = new Room("labStorage", "Lab Storage", "Shelves of outdated hardware and chemicals.");
        Room brokenElevator = new Room("brokenElevator", "Broken Elevator", "The elevator doors hang over a dark shaft.");
        Room serverCloset = new Room("serverCloset", "Server Closet", "A warm humming room full of blinking servers.");
        Room examArchive = new Room("examArchive", "Exam Archive", "Cabinets of old exams and paperwork.");
        Room deanVault = new Room("deanVault", "Dean Vault", "A hidden chamber guarded by university secrets.");
        Room finalChamber = new Room("finalChamber", "Final Chamber", "A spectral exam hall where the boss waits.");

        stairwell.connect(Direction.EAST, lectureHall);

        lectureHall.connect(Direction.WEST, stairwell);
        lectureHall.connect(Direction.EAST, labStorage);
        lectureHall.connect(Direction.SOUTH, brokenElevator);
        lectureHall.connect(Direction.NORTH, examArchive);

        labStorage.connect(Direction.WEST, lectureHall);
        labStorage.connect(Direction.NORTH, serverCloset);

        brokenElevator.connect(Direction.NORTH, lectureHall);

        serverCloset.connect(Direction.SOUTH, labStorage);
        serverCloset.connect(Direction.EAST, deanVault);

        examArchive.connect(Direction.SOUTH, lectureHall);
        examArchive.connect(Direction.EAST, deanVault);

        deanVault.connect(Direction.WEST, examArchive);
        deanVault.connect(Direction.SOUTH, serverCloset);
        deanVault.connect(Direction.NORTH, finalChamber);

        finalChamber.connect(Direction.SOUTH, deanVault);

        examArchive.setLocked(true, "Archive Key");
        finalChamber.setLocked(true, "Vault Key");

        brokenElevator.setTrap(new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true));

        lectureHall.addItem(new Potion("Coffee Potion", "A suspiciously warm cup of coffee.", 15));
        labStorage.addItem(new Key("Archive Key", "Opens the Exam Archive."));
        labStorage.addItem(new Armor("Calculator Shield", "A battered calculator used as a shield.", 3));
        serverCloset.addItem(new Weapon("Stapler of Justice", "A heavy-duty stapler with righteous force.", 4));
        examArchive.addItem(new QuestItem("Lost Gradebook", "The legendary missing gradebook."));
        deanVault.addItem(new Key("Vault Key", "Opens the Final Chamber."));

        lectureHall.addMonster(new Monster("Sleep-Deprived TA", 18, 6, 1,
                List.of(new Potion("Coffee Potion", "A backup coffee drop.", 15))));
        serverCloset.addMonster(new Monster("Spreadsheet Golem", 28, 7, 4,
                List.of(new Weapon("Dry Erase Sword", "An improvised marker tray blade.", 6))));
        examArchive.addMonster(new Monster("Plagiarism Ghost", 22, 8, 2, List.of()));
        deanVault.addMonster(new Monster("Registrar Wraith", 30, 9, 3,
                List.of(new Armor("Graduation Gown Armor", "Ceremonial robes with uncanny resilience.", 5))));
        finalChamber.addMonster(new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3));

        Player player = new Player("Student Explorer", 50, 7, 2, new Inventory(8));
        player.setCurrentRoom(stairwell);

        Quest quest = new Quest(
                "Escape the Basement",
                "Recover the Lost Gradebook and defeat the Final Exam Phantom."
        );

        QuestTracker questTracker = new QuestTracker();
        MovementEngine movementEngine = new MovementEngine(new TrapResolver());
        CombatEngine combatEngine = new CombatEngine(new DamageCalculator(), questTracker);
        InteractionEngine interactionEngine = new InteractionEngine(questTracker);

        return new GameEngine(player, quest, movementEngine, combatEngine, interactionEngine);
    }
}
