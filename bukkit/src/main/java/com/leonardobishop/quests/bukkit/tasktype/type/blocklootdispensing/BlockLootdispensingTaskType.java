package com.leonardobishop.quests.bukkit.tasktype.type.blocklootdispensing;

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
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class BlockLootdispensingTaskType extends BukkitTaskType<BlockLootdispensingTask> {

    private final BukkitQuestsPlugin plugin;

    public BlockLootdispensingTaskType(BukkitQuestsPlugin plugin) {
        super("blocklootdispensing", TaskUtils.TASK_ATTRIBUTION_STRING, "Dispense certain amount of loot from a block.", BlockLootdispensingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDispenseLoot(BlockDispenseLootEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();
        List<ItemStack> loots = event.getDispensedLoot();

        if (loots.isEmpty()) {
            handle(player, qPlayer, block, null, 0);
        }

        for (int i = 0; i < loots.size(); i++) {
            handle(player, qPlayer, block, loots.get(i), i);
        }
    }

    private void handle(Player player, QPlayer qPlayer, Block block, ItemStack item, int i) {
        for (TaskUtils.PendingTask<BlockLootdispensingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BlockLootdispensingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player dispensed loot from block", quest.getId(), task.getId(), player.getUniqueId());

            final int amountToIncrease;

            if (task.countDispenses) {
                if (i != 0) {
                    continue;
                }

                amountToIncrease = 1;
            } else {
                if (task.itemRequirement != null) {
                    if (item == null) {
                        super.debug("Specific item is required, dropped item is null; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    super.debug("Specific item is required; dropped item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                    if (!task.itemRequirement.matches(item)) {
                        super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    } else {
                        super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }

                amountToIncrease = item != null ? item.getAmount() : 0;
            }

            if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, amountToIncrease);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
