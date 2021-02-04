package com.leonardobishop.quests;

import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

public class QuestCompleter implements Runnable {

    private final Queue<QuestProgress> completionQueue = new LinkedList<>();
    private final Queue<QuestProgressFile> fullCheckQueue = new LinkedList<>();
    private final Quests plugin;

    public QuestCompleter(Quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.processCompletionQueue();
        this.processFullCheckQueue();
    }

    private void processCompletionQueue() {
        QuestProgress questProgress = completionQueue.poll();
        if (questProgress == null) return;

        Player player = Bukkit.getPlayer(questProgress.getPlayer());
        if (player != null && player.isOnline()) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());

            if (!questProgressFile.hasStartedQuest(quest)) return;

            if (checkComplete(quest, questProgress)) {
                questProgressFile.completeQuest(quest);
            }
        }
    }

    private void processFullCheckQueue() {
        QuestProgressFile questProgressFile = fullCheckQueue.poll();
        if (questProgressFile == null) return;

        Player player = Bukkit.getPlayer(questProgressFile.getPlayerUUID());
        if (player != null && player.isOnline()) {
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

    private boolean checkComplete(Quest quest, QuestProgress questProgress) {
        boolean complete = true;
        for (Task task : quest.getTasks()) {
            TaskProgress taskProgress;
            if ((taskProgress = questProgress.getTaskProgress(task.getId())) == null || !taskProgress.isCompleted()) {
                complete = false;
                break;
            }
        }

        return complete;
    }

    public void queueSingular(QuestProgress questProgress) {
        completionQueue.add(questProgress);
    }

    public void queueFullCheck(QuestProgressFile questProgressFile) {
        fullCheckQueue.add(questProgressFile);
    }
}
