package com.leonardobishop.quests.bukkit.tasktype.type.permission;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PermissionTaskType extends BukkitTaskType<PermissionTask> {

    private final BukkitQuestsPlugin plugin;
    private WrappedTask poll;

    public PermissionTaskType(BukkitQuestsPlugin plugin) {
        super("permission", TaskUtils.TASK_ATTRIBUTION_STRING, "Test if a player has a permission", PermissionTask.SCHEMA);
        this.plugin = plugin;
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
                    for (TaskUtils.PendingTask<PermissionTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, PermissionTaskType.this)) {
                        Quest quest = pendingTask.quest();
                        PermissionTask task = pendingTask.task();
                        TaskProgress taskProgress = pendingTask.taskProgress();

                        PermissionTaskType.super.debug("Polling permissions for player", quest.getId(), task.getId(), player.getUniqueId());

                        PermissionTaskType.super.debug("Checking permission '" + task.permission + "'", quest.getId(), task.getId(), player.getUniqueId());
                        if (player.hasPermission(task.permission)) {
                            PermissionTaskType.super.debug("Player has permission", quest.getId(), task.getId(), player.getUniqueId());
                            PermissionTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                            taskProgress.setCompleted(true);
                        } else {
                            PermissionTaskType.super.debug("Player does not have permission", quest.getId(), task.getId(), player.getUniqueId());
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
