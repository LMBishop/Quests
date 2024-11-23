package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class QPlayerData {

    private final UUID playerUUID;
    private final QPlayerPreferences playerPreferences;
    private final QuestProgressFile questProgressFile;

    public QPlayerData(final @NotNull UUID playerUUID, final @NotNull QPlayerPreferences playerPreferences, final @NotNull QuestProgressFile questProgressFile) {
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID cannot be null");
        this.playerPreferences = Objects.requireNonNull(playerPreferences, "playerPreferences cannot be null");
        this.questProgressFile = Objects.requireNonNull(questProgressFile, "questProgressFile cannot be null");
    }

    public QPlayerData(final @NotNull QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        this.playerUUID = playerData.playerUUID;
        this.playerPreferences = playerData.playerPreferences;
        this.questProgressFile = new QuestProgressFile(playerData.questProgressFile);
    }

    public @NotNull UUID playerUUID() {
        return this.playerUUID;
    }

    public @NotNull QPlayerPreferences playerPreferences() {
        return this.playerPreferences;
    }

    public @NotNull QuestProgressFile questProgressFile() {
        return this.questProgressFile;
    }

    public void setModified(final boolean modified) {
        this.questProgressFile.setModified(modified);
    }
}
