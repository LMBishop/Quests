package com.leonardobishop.quests.common.storage;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;

import java.util.UUID;

public interface StorageProvider {

    void init();

    void shutdown();

    QuestProgressFile loadProgressFile(UUID uuid);

    void saveProgressFile(UUID uuid, QuestProgressFile questProgressFile);

}
