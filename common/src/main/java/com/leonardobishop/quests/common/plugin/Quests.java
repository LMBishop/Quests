package com.leonardobishop.quests.common.plugin;

import com.leonardobishop.quests.common.config.QuestsConfig;
import com.leonardobishop.quests.common.logger.QuestsLogger;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.quest.QuestCompleter;
import com.leonardobishop.quests.common.quest.QuestManager;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.scheduler.ServerScheduler;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import com.leonardobishop.quests.common.updater.Updater;
import org.jetbrains.annotations.NotNull;

public interface Quests {

    /**
     * Obtain an instance of the Quests logger.
     *
     * @see QuestsLogger
     * @return quests logger
     */
    @NotNull QuestsLogger getQuestsLogger();

    /**
     * Obtain an instance of the QuestManager.
     *
     * @see QuestManager
     * @return quest manager
     */
    @NotNull QuestManager getQuestManager();

    /**
     * Obtain an instance of the QPlayerManager.
     *
     * @see QPlayerManager
     * @return quest player manager
     */
    @NotNull QPlayerManager getPlayerManager();

    /**
     * Obtain an instance of the QuestController.
     *
     * @see QuestController
     * @return quest controller
     */
    @NotNull QuestController getQuestController();

    /**
     * Obtain an instance of the TaskTypeManager.
     *
     * @see TaskTypeManager
     * @return task type manager
     */
    @NotNull TaskTypeManager getTaskTypeManager();

    /**
     * Obtain an instance of the QuestCompleter.
     *
     * @see QuestCompleter
     * @return quest completer
     */
    @NotNull QuestCompleter getQuestCompleter();

    /**
     * Obtain an instance of the QuestConfig.
     *
     * @see QuestsConfig
     * @return quest config
     */
    @NotNull QuestsConfig getQuestsConfig();

    /**
     * Obtain an instance of the Updater.
     *
     * @see Updater
     * @return updater
     */
    @NotNull Updater getUpdater();

    /**
     * Obtain an instance of the ServerScheduler.
     *
     * @see ServerScheduler
     * @return server scheduler
     */
    @NotNull ServerScheduler getScheduler();

    /**
     * Obtain an instance of the StorageProvider.
     *
     * @see StorageProvider
     * @return storage provider
     */
    @NotNull StorageProvider getStorageProvider();

    /**
     * Performs a full reload of the plugin, unloading and re-registering quests to their task types.
     */
    void reloadQuests();

}
