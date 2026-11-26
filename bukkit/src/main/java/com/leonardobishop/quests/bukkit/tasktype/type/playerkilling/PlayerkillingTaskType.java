package com.leonardobishop.quests.bukkit.tasktype.type.playerkilling;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerkillingTaskType extends BukkitTaskType<PlayerkillingTask> {

    private final BukkitQuestsPlugin plugin;

    public PlayerkillingTaskType(BukkitQuestsPlugin plugin) {
        super("playerkilling", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of players.", PlayerkillingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        EntityDamageEvent damageEvent = killed.getLastDamageCause();
        Player killer = plugin.getVersionSpecificHandler().getDamager(damageEvent);

        if (killer == null || killer.hasMetadata("NPC") || killer == killed) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<PlayerkillingTask> pendingTask : TaskUtils.getApplicableTasks(killer, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            PlayerkillingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(killer, quest, task, pendingTask, task.amount);
        }
    }
}
