package com.example.haunted.rules;

import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;

public class QuestTracker {
    public void updateQuestForItem(Quest quest, Item item) {
        if (item != null && "Lost Gradebook".equalsIgnoreCase(item.getName())) {
            quest.markGradebookRecovered();
        }
    }

    public void updateQuestForMonster(Quest quest, Monster monster) {
        if (monster != null && "Final Exam Phantom".equalsIgnoreCase(monster.getName()) && !monster.isAlive()) {
            quest.markPhantomDefeated();
        }
    }
}
