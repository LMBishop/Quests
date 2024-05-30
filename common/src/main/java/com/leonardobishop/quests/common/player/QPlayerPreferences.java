package com.leonardobishop.quests.common.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class QPlayerPreferences {

    private static Set<QPlayer> debuggers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Map<String, DebugType> debug = new HashMap<>();
    private String trackedQuestId;

    public QPlayerPreferences(final @Nullable String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }

    public @Nullable String getTrackedQuestId() {
        return this.trackedQuestId;
    }

    public void setTrackedQuestId(final @Nullable String trackedQuestId) {
        this.trackedQuestId = trackedQuestId;
    }

    public @Nullable DebugType getDebug(final @NotNull String questId) {
        return this.debug.getOrDefault(questId, this.debug.get("*"));
    }

    public void setDebug(final @NotNull String questId, final @NotNull DebugType debugType) {
        this.debug.put(questId, debugType);
    }

    public void unsetDebug(final @NotNull String questId) {
        this.debug.remove(questId);
    }

    public boolean isDebug() {
        return !this.debug.isEmpty();
    }

    public enum DebugType {
        SELF,
        ALL
    }

    public static @NotNull Set<QPlayer> getDebuggers() {
        return QPlayerPreferences.debuggers;
    }

    public static void setDebuggers(final @NotNull Set<QPlayer> debuggers) {
        QPlayerPreferences.debuggers = debuggers;
    }
}
