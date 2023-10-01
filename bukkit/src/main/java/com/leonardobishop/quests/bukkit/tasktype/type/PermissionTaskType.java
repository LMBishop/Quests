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

public final class PermissionTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private WrappedTask poll;

    public PermissionTaskType(BukkitQuestsPlugin plugin) {
        super("permission", TaskUtils.TASK_ATTRIBUTION_STRING, "Test if a player has a permission");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "permission"));
    }

    @Override
    public void onReady() {
        this.poll = new WrappedRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        continue;
                    }
                    for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, PermissionTaskType.this)) {
                        Quest quest = pendingTask.quest();
                        Task task = pendingTask.task();
                        TaskProgress taskProgress = pendingTask.taskProgress();

                        PermissionTaskType.super.debug("Polling permissions for player", quest.getId(), task.getId(), player.getUniqueId());

                        String permission = (String) task.getConfigValue("permission");
                        if (permission != null) {
                            PermissionTaskType.super.debug("Checking permission '" + permission + "'", quest.getId(), task.getId(), player.getUniqueId());
                            if (player.hasPermission(permission)) {
                                PermissionTaskType.super.debug("Player has permission", quest.getId(), task.getId(), player.getUniqueId());
                                PermissionTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                taskProgress.setCompleted(true);
                            } else {
                                PermissionTaskType.super.debug("Player does not have permission", quest.getId(), task.getId(), player.getUniqueId());
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin.getScheduler(), 30L, 30L);
    }

    @Override
    public void onDisable() {
        if (this.poll != null) {
            this.poll.cancel();
        }
    }
}
