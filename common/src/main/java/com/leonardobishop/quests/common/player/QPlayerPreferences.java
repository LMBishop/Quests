package com.leonardobishop.quests.common.player;

public class QPlayerPreferences {

    private String trackedQuestId;

    public QPlayerPreferences(String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }

    public String getTrackedQuestId() {
        return trackedQuestId;
    }

    public void setTrackedQuestId(String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }
}
