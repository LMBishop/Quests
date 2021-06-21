package com.leonardobishop.quests.common.storage;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;

import java.util.UUID;

/**
 * The storage provider is responsible for obtaining a QuestProgressFile for a specified UUID and for
 * writing a QuestProgressFile.
 */
public interface StorageProvider {

    void init();

    void shutdown();

    QuestProgressFile loadProgressFile(UUID uuid);

    void saveProgressFile(UUID uuid, QuestProgressFile questProgressFile);

}
