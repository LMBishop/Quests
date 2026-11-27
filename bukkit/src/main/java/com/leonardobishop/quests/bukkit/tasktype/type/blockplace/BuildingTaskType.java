package com.leonardobishop.quests.bukkit.tasktype.type.blockplace;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BuildingTaskType extends BukkitTaskType<BuildingTask> {

    private final BukkitQuestsPlugin plugin;

    public BuildingTaskType(BukkitQuestsPlugin plugin) {
        super("blockplace", TaskUtils.TASK_ATTRIBUTION_STRING, "Place a set amount of a block.", BuildingTask.SCHEMA, "blockplacecertain");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();

        for (TaskUtils.PendingTask<BuildingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BuildingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player placed block " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }

    // subtract if enabled
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();

        for (TaskUtils.PendingTask<BuildingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BuildingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player mined block " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (!task.reverseIfBroken) {
                continue;
            }

            super.debug("reverse-if-broken is enabled, checking block", quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int currentProgress = TaskUtils.getIntegerTaskProgress(taskProgress);
            if (currentProgress <= 0 && !task.allowNegativeProgress) {
                super.debug("Task progress is already at zero and negative progress is disabled, skipping decrement", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.decrementIntegerTaskProgress(taskProgress);
            super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
