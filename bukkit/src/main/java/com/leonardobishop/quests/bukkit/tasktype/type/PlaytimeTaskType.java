package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaytimeTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private WrappedTask poll;

    public PlaytimeTaskType(BukkitQuestsPlugin plugin) {
        super("playtime", TaskUtils.TASK_ATTRIBUTION_STRING, "Track the amount of playing time a user has been on");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "minutes"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "minutes"));
    }


    @Override
    public void onReady() {
        if (this.poll == null) {
            this.poll = new WrappedRunnable() {
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

                            boolean ignoreAfk = TaskUtils.getConfigBoolean(task, "ignore-afk");

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

                            TaskUtils.sendTrackAdvancement(player, quest, task, taskProgress, minutes);
                        }
                    }
                }
            }.runTaskTimer(plugin.getScheduler(), 1200L, 1200L);
        }
    }

    @Override
    public void onDisable() {
//        if (this.poll != null) {
//            this.poll.cancel();
//        }
    }
}
