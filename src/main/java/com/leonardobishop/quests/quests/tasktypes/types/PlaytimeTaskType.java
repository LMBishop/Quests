package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaytimeTaskType extends TaskType {

    public PlaytimeTaskType() {
        super("playtime", "Reinatix", "Track the amount of playing time a user has been on");
        playTime();
    }

    public void playTime() {
        Bukkit.getScheduler().runTaskTimer(Quests.get(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId());
                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                for (Quest quest : super.getRegisteredQuests()) {
                    if (questProgressFile.hasStartedQuest(quest)) {
                        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
                        for (Task task : quest.getTasksOfType(super.getType())) {
                            TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
                            if (taskProgress.isCompleted()) {
                                continue;
                            }
                            int minutes = (int) task.getConfigValue("minutes");
                            if (taskProgress.getProgress() == null) {
                                taskProgress.setProgress(1);
                            } else {
                                taskProgress.setProgress((int) taskProgress.getProgress() + 1);
                            }
                            if (((int) taskProgress.getProgress()) >= minutes) {
                                taskProgress.setCompleted(true);
                            }
                        }
                    }
                }
            }
        }, 1200L, 1200L);
    }
}
