package com.leonardobishop.quests.bukkit.tasktype.type.walking;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.List;

public final class WalkingTaskType extends BukkitTaskType<WalkingTask> {

    private final BukkitQuestsPlugin plugin;

    public WalkingTaskType(BukkitQuestsPlugin plugin) {
        super("walking", TaskUtils.TASK_ATTRIBUTION_STRING, "Walk a set distance.", WalkingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        int distance = Math.abs(to.getBlockX() - from.getBlockX()) + Math.abs(to.getBlockZ() - from.getBlockZ());
        if (distance == 0) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getVehicle() instanceof RideableMinecart) {
            return; // minecarts movement is already handled by VehicleMoveEvent
        }

        handle(player, distance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        int distance = Math.abs(to.getBlockX() - from.getBlockX()) + Math.abs(to.getBlockZ() - from.getBlockZ());
        if (distance == 0) {
            return;
        }

        List<Entity> passengers = this.plugin.getVersionSpecificHandler().getPassengers(event.getVehicle());
        for (Entity passenger : passengers) {
            if (passenger instanceof Player player) {
                handle(player, distance);
            }
        }
    }

    private void handle(Player player, int distance) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<WalkingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            WalkingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            MODE_CHECK:
            if (!task.modes.isEmpty()) {
                for (WalkingMode mode : task.modes) {
                    if (mode.validate(this.plugin, player)) {
                        break MODE_CHECK;
                    }
                }

                super.debug("Player mode does not match required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, distance);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.distance) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.distance);
        }
    }
}
