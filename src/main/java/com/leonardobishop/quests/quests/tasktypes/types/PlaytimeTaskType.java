package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PlaytimeTaskType extends TaskType {

    private BukkitTask poll;
    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public PlaytimeTaskType() {
        super("playtime", "Reinatix", "Track the amount of playing time a user has been on");
        this.creatorConfigValues.add(new ConfigValue("minutes", true, "Time in minutes."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".minutes", config.get("minutes"), problems, "minutes", super.getType()))
            TaskUtils.configValidateInt(root + ".minutes", config.get("minutes"), problems, false, true, "minutes");
        return problems;
    }


    @Override
    public void onReady() {
        if (this.poll == null) {
            this.poll = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
                        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                        for (Quest quest : PlaytimeTaskType.super.getRegisteredQuests()) {
                            if (questProgressFile.hasStartedQuest(quest)) {
                                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
                                for (Task task : quest.getTasksOfType(PlaytimeTaskType.super.getType())) {
                                    if (!TaskUtils.validateWorld(player, task)) continue;

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
                }
            }.runTaskTimer(Quests.get(), 1200L, 1200L);
        }
    }

    @Override
    public void onDisable() {
//        if (this.poll != null) {
//            this.poll.cancel();
//        }
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }
}
