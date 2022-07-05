package com.leonardobishop.quests.common.player;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class QPlayerPreferences {

    private final Map<String, DebugType> debug = new HashMap<>();
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

    public DebugType getDebug(String questId) {
        return debug.get(questId);
    }

    public void setDebug(String questId, DebugType debugType) {
        debug.put(questId, debugType);
    }

    public enum DebugType {
        SELF,
        ALL
    }
}
