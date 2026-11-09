package com.example.haunted.rules;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;

public class DamageCalculator {
    public int calculatePlayerDamage(Player player, Monster monster) {
        return Math.max(1, player.getAttackPower() - monster.getDefense());
    }

    public int calculateMonsterDamage(Monster monster, Player player) {
        int attack = monster instanceof BossMonster bossMonster ? bossMonster.getCurrentAttack() : monster.getAttack();
        return Math.max(1, attack - player.getDefensePower());
    }
}
