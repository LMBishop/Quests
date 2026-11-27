package com.leonardobishop.quests.bukkit.tasktype.type.hatching;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.projectiles.ProjectileSource;

public final class HatchingTaskType extends BukkitTaskType<HatchingTask> {

    private final BukkitQuestsPlugin plugin;

    public HatchingTaskType(BukkitQuestsPlugin plugin) {
        super("hatching", TaskUtils.TASK_ATTRIBUTION_STRING, "Hatch a set amount of entities of certain types.", HatchingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onThrownEggHatch(ThrownEggHatchEvent event) {
        int numHatches = event.getNumHatches();
        if (numHatches == 0) {
            return;
        }

        ProjectileSource shooter = event.getEgg().getShooter();
        if (!(shooter instanceof Player player)) {
            return;
        }

        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        EntityType hatchingType = event.getHatchingType();

        for (TaskUtils.PendingTask<HatchingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            HatchingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player hatched " + hatchingType, quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchEntity(this, pendingTask, task.mobs, hatchingType, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, numHatches);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
