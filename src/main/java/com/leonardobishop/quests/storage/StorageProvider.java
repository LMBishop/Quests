package com.leonardobishop.quests.storage;

import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;

import java.util.UUID;

public interface StorageProvider {

    void init();
    void shutdown();
    QuestProgressFile loadProgressFile(UUID uuid);
    void saveProgressFile(UUID uuid, QuestProgressFile questProgressFile);

}
