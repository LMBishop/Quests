package com.leonardobishop.quests.common.plugin;

import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.logger.QuestsLogger;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.quest.QuestCompleter;
import com.leonardobishop.quests.common.quest.QuestManager;
import com.leonardobishop.quests.common.scheduler.ServerScheduler;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import com.leonardobishop.quests.common.updater.Updater;
import com.leonardobishop.quests.common.config.QuestsConfig;

public interface Quests {

    QuestsLogger getQuestsLogger();

    QuestManager getQuestManager();

    QPlayerManager getPlayerManager();

    QuestController getQuestController();

    TaskTypeManager getTaskTypeManager();

    QuestCompleter getQuestCompleter();

    QuestsConfig getQuestsConfig();

    Updater getUpdater();

    ServerScheduler getScheduler();

    StorageProvider getStorageProvider();

    void reloadQuests();

}
