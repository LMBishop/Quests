package com.leonardobishop.quests.common.player;

import org.jetbrains.annotations.Nullable;

public class QPlayerPreferences {

    private String trackedQuestId;

    public QPlayerPreferences(String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }

    public @Nullable String getTrackedQuestId() {
        return trackedQuestId;
    }

    public void setTrackedQuestId(@Nullable String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }
}
