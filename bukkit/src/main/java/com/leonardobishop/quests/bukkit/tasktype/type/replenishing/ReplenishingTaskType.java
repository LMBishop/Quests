package com.leonardobishop.quests.bukkit.tasktype.type.replenishing;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableEntityInventory;
import com.destroystokyo.paper.loottable.LootableInventory;
import com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class ReplenishingTaskType extends BukkitTaskType<ReplenishingTask> {

    private final BukkitQuestsPlugin plugin;

    public ReplenishingTaskType(BukkitQuestsPlugin plugin) {
        super("replenishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Replenish a set amount of certain blocks or entities.", ReplenishingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLootableInventoryReplenish(LootableInventoryReplenishEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        final LootableInventory inventory = event.getInventory();
        if (!inventory.hasLootTable()) {
            return;
        }

        final Block block = inventory instanceof final LootableBlockInventory blockInventory ? blockInventory.getBlock() : null;
        final Entity entity = inventory instanceof final LootableEntityInventory entityInventory ? entityInventory.getEntity() : null;

        for (TaskUtils.PendingTask<ReplenishingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            ReplenishingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (block != null) {
                super.debug("Player replenished a block, current block is " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());
                if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, block, player.getUniqueId())) {
                    super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else if (entity != null) {
                super.debug("Player replenished an entity, current entity is " + entity.getType(), quest.getId(), task.getId(), player.getUniqueId());
                if (!TaskUtils.matchEntity(this, pendingTask, task.mobs, entity, player.getUniqueId())) {
                    super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
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
}
