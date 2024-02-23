package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BuildingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BuildingTaskType(BukkitQuestsPlugin plugin) {
        super("blockplace", TaskUtils.TASK_ATTRIBUTION_STRING, "Place a set amount of a block.", "blockplacecertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, TaskUtils.MaterialListConfigValidatorMode.BLOCK, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "reverse-if-broken"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-negative-progress"));
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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player placed block " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");
            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player mined block " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());

            boolean reverseIfBroken = TaskUtils.getConfigBoolean(task, "reverse-if-broken");
            if (!reverseIfBroken) {
                continue;
            }

            super.debug("reverse-if-broken is enabled, checking block", quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            boolean allowNegativeProgress = TaskUtils.getConfigBoolean(task, "allow-negative-progress", true);
            int currentProgress = TaskUtils.getIntegerTaskProgress(taskProgress);
            if (currentProgress <= 0 && !allowNegativeProgress) {
                super.debug("Task progress is already at zero and negative progress is disabled, skipping decrement", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.decrementIntegerTaskProgress(taskProgress);
            super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");
            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
