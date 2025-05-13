package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.util.Modern;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.UUID;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class QPlayerData {

    private final UUID playerUUID;
    private final QPlayerPreferences playerPreferences;
    private final QuestProgressFile questProgressFile;

    public QPlayerData(final UUID playerUUID, final QPlayerPreferences playerPreferences, final QuestProgressFile questProgressFile) {
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID cannot be null");
        this.playerPreferences = Objects.requireNonNull(playerPreferences, "playerPreferences cannot be null");
        this.questProgressFile = Objects.requireNonNull(questProgressFile, "questProgressFile cannot be null");
    }

    public QPlayerData(final QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        this.playerUUID = playerData.playerUUID;
        this.playerPreferences = playerData.playerPreferences;
        this.questProgressFile = new QuestProgressFile(playerData.questProgressFile);
    }

    public UUID playerUUID() {
        return this.playerUUID;
    }

    public QPlayerPreferences playerPreferences() {
        return this.playerPreferences;
    }

    public QuestProgressFile questProgressFile() {
        return this.questProgressFile;
    }

    public void setModified(final boolean modified) {
        this.questProgressFile.setModified(modified);
    }
}
