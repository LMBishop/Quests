package com.leonardobishop.quests;

import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;

import java.util.Map;

public class QuestCompleter implements Runnable {

    private final Quests plugin;

    public QuestCompleter(Quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //TODO if it still runs like shit then maybe only process a few players per X ticks rather than the whole server in one go
        for (QPlayer qPlayer : plugin.getPlayerManager().getQPlayers()) {
            if (qPlayer.isOnlyDataLoaded()) {
                continue;
            }
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            for (QuestProgress questProgress : questProgressFile.getAllQuestProgress()) {
                Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
                if (quest == null) continue;
                if (!questProgressFile.hasStartedQuest(quest)) continue;

                boolean complete = true;
                for (Task task : quest.getTasks()) {
                    TaskProgress taskProgress;
                    if ((taskProgress = questProgress.getTaskProgress(task.getId())) == null || !taskProgress.isCompleted()) {
                        complete = false;
                        break;
                    }
                }
                if (complete) {
                    questProgressFile.completeQuest(quest);
                }
            }
        }
    }
}