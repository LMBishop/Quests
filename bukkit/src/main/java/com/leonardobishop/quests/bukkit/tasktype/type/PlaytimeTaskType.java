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

                        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, PlaytimeTaskType.this)) {
                            Quest quest = pendingTask.quest();
                            Task task = pendingTask.task();
                            TaskProgress taskProgress = pendingTask.taskProgress();

                            PlaytimeTaskType.super.debug("Polling playtime for player", quest.getId(), task.getId(), player.getUniqueId());

                            boolean ignoreAfk = (boolean) task.getConfigValue("ignore-afk", false);

                            if (ignoreAfk && plugin.getEssentialsHook() == null) {
                                PlaytimeTaskType.super.debug("ignore-afk is enabled, but Essentials is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                            }

                            if (ignoreAfk
                                    && plugin.getEssentialsHook() != null
                                    && plugin.getEssentialsHook().isAfk(player)) {
                                PlaytimeTaskType.super.debug("ignore-afk is enabled and Essentials reports player as afk, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                                continue;
                            }

                            int minutes = (int) task.getConfigValue("minutes");
                            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                            PlaytimeTaskType.super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                            if (progress >= minutes) {
                                PlaytimeTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                taskProgress.setCompleted(true);
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
