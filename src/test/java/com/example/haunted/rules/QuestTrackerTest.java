package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.QuestStatus;

class QuestTrackerTest {

    private QuestTracker tracker;
    private Quest quest;

    @BeforeEach
    void setUp() {
        tracker = new QuestTracker();
        quest = new Quest("Escape the Basement", "Recover the gradebook and defeat the phantom.");
    }

    // ---------- picking up items ----------

    // Name matching ignores upper/lower case
    @ParameterizedTest
    @ValueSource(strings = {"Lost Gradebook", "lost gradebook", "LOST GRADEBOOK"})
    void pickingUpGradebookMarksItRecovered(String name) {
        tracker.updateQuestForItem(quest, new QuestItem(name, "the gradebook"));

        assertTrue(quest.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Coffee Potion", "Gradebook", "Lost Grade"})
    void otherItemsDoNotAffectQuest(String name) {
        tracker.updateQuestForItem(quest, new QuestItem(name, "not the gradebook"));

        assertFalse(quest.isGradebookRecovered());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void nullItemIsIgnored() {
        tracker.updateQuestForItem(quest, null);

        assertFalse(quest.isGradebookRecovered());
    }

    // ---------- defeating monsters ----------

    @ParameterizedTest
    @ValueSource(strings = {"Final Exam Phantom", "final exam phantom", "FINAL EXAM PHANTOM"})
    void defeatedPhantomCountsForQuest(String name) {
        BossMonster phantom = new BossMonster(name, 40, 10, 4, List.of(), 3);
        phantom.takeDamage(40);                                   // now dead

        tracker.updateQuestForMonster(quest, phantom);

        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
    }

    @Test
    void phantomStillAliveDoesNotCount() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(39);                                   // boundary: 1 health left

        tracker.updateQuestForMonster(quest, phantom);

        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void otherDefeatedMonsterDoesNotCount() {
        Monster ghost = new Monster("Plagiarism Ghost", 22, 8, 2, List.of());
        ghost.takeDamage(22);

        tracker.updateQuestForMonster(quest, ghost);

        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void nullMonsterIsIgnored() {
        tracker.updateQuestForMonster(quest, null);

        assertFalse(quest.isPhantomDefeated());
    }

    // ---------- quest completion ----------

    @Test
    void gradebookAndPhantomTogetherCompleteQuest() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(40);

        tracker.updateQuestForItem(quest, new QuestItem("Lost Gradebook", "the gradebook"));
        tracker.updateQuestForMonster(quest, phantom);

        assertTrue(quest.isComplete());
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
    }
}