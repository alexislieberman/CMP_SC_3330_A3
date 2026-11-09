package com.example.haunted.model;

import java.util.Objects;

public class Quest {
    private final String name;
    private final String description;
    private QuestStatus status;
    private boolean gradebookRecovered;
    private boolean phantomDefeated;

    public Quest(String name, String description) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.status = QuestStatus.NOT_STARTED;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public boolean isGradebookRecovered() {
        return gradebookRecovered;
    }

    public boolean isPhantomDefeated() {
        return phantomDefeated;
    }

    public void markGradebookRecovered() {
        this.gradebookRecovered = true;
        updateStatus();
    }

    public void markPhantomDefeated() {
        this.phantomDefeated = true;
        updateStatus();
    }

    public void updateStatus() {
        if (gradebookRecovered && phantomDefeated) {
            status = QuestStatus.COMPLETED;
        } else if (gradebookRecovered || phantomDefeated) {
            status = QuestStatus.IN_PROGRESS;
        }
    }

    public boolean isComplete() {
        return status == QuestStatus.COMPLETED;
    }
}
