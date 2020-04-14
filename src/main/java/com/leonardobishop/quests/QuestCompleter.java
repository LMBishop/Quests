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
        for (QPlayer qPlayer : plugin.getPlayerManager().getQPlayers()) {
            if (qPlayer.isOnlyDataLoaded()) {
                continue;
            }
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                Quest quest = entry.getValue();
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
                if (questProgressFile.hasStartedQuest(quest)) {
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
}
