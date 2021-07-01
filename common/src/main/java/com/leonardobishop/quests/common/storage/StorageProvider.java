package com.leonardobishop.quests.common.storage;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The storage provider is responsible for obtaining a QuestProgressFile for a specified UUID and for
 * writing a QuestProgressFile.
 */
public interface StorageProvider {

    void init();

    void shutdown();

    /**
     * Load a QuestProgressFile from the data source by a specific UUID
     *
     * @param uuid the UUID to load
     * @return {@link QuestProgressFile} or null
     */
    @Nullable QuestProgressFile loadProgressFile(@NotNull UUID uuid);

    /**
     * Save a QuestProgressFile to the data source with a specific UUID
     * @param uuid the uuid to match the file to
     * @param questProgressFile the file to save
     */
    void saveProgressFile(@NotNull UUID uuid, @NotNull QuestProgressFile questProgressFile);

}
