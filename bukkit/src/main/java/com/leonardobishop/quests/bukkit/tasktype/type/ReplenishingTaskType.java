package com.leonardobishop.quests.bukkit.tasktype.type;

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
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class ReplenishingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ReplenishingTaskType(BukkitQuestsPlugin plugin) {
        super("replenishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Replenish a set amount of certain blocks or entities.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, TaskUtils.MaterialListConfigValidatorMode.BLOCK, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useEntityListConfigValidator(this, "mob", "mobs"));
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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (block != null) {
                super.debug("Player replenished a block, current block is " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());
                if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                    super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else if (entity != null) {
                super.debug("Player replenished an entity, current entity is " + entity.getType(), quest.getId(), task.getId(), player.getUniqueId());
                if (!TaskUtils.matchEntity(this, pendingTask, entity, player.getUniqueId())) {
                    super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
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
}
