package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Weapon;

class DamageCalculatorTest {

    private DamageCalculator calculator;

    // Runs before EVERY test so each one starts fresh
    @BeforeEach
    void setUp() {
        calculator = new DamageCalculator();
    }

    // One test, many inputs: attack, defense, expected damage
    @ParameterizedTest
    @CsvSource({
        "10, 3, 7",   // normal case
        "6, 4, 2",
        "5, 4, 1",    // boundary: difference is exactly 1
        "5, 5, 1",    // difference 0 -> minimum damage is 1
        "5, 9, 1"     // defense higher -> still 1
    })
    void playerDamageIsAttackMinusDefenseWithMinimumOne(int attack, int defense, int expected) {
        Player player = new Player("Hero", 50, attack, 0, new Inventory(5));
        Monster monster = new Monster("Ghost", 30, 0, defense, List.of());

        assertEquals(expected, calculator.calculatePlayerDamage(player, monster));
    }

    @Test
    void weaponBonusIsAddedToPlayerDamage() {
        Player player = new Player("Hero", 50, 7, 2, new Inventory(5));
        player.equipWeapon(new Weapon("Stapler", "", 4));       // attack becomes 11
        Monster monster = new Monster("Golem", 28, 7, 4, List.of());

        assertEquals(7, calculator.calculatePlayerDamage(player, monster)); // 11 - 4
    }

    @Test
    void bossHitsHarderAtHalfHealth() {
        Player player = new Player("Hero", 50, 7, 2, new Inventory(5));
        BossMonster boss = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);

        assertEquals(8, calculator.calculateMonsterDamage(boss, player));  // 10 - 2

        boss.takeDamage(20);                                              // now exactly half
        assertEquals(11, calculator.calculateMonsterDamage(boss, player)); // (10 + 3) - 2
    }
}
