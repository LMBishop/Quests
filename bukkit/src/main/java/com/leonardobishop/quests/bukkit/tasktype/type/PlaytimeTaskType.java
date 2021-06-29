package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PlaytimeTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private BukkitTask poll;

    public PlaytimeTaskType(BukkitQuestsPlugin plugin) {
        super("playtime", TaskUtils.TASK_ATTRIBUTION_STRING, "Track the amount of playing time a user has been on");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
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
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                        if (qPlayer == null) {
                            continue;
                        }

                        for (Quest quest : PlaytimeTaskType.super.getRegisteredQuests()) {
                            if (qPlayer.hasStartedQuest(quest)) {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
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
            }.runTaskTimer(plugin, 1200L, 1200L);
        }
    }

    @Override
    public void onDisable() {
//        if (this.poll != null) {
//            this.poll.cancel();
//        }
    }

}
