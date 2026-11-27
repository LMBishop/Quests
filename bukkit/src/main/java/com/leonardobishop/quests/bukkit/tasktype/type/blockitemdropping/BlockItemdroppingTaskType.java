package com.leonardobishop.quests.bukkit.tasktype.type.blockitemdropping;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class BlockItemdroppingTaskType extends BukkitTaskType<BlockItemdroppingTask> {

    private final BukkitQuestsPlugin plugin;

    public BlockItemdroppingTaskType(BukkitQuestsPlugin plugin) {
        super("blockitemdropping", TaskUtils.TASK_ATTRIBUTION_STRING, "Drop certain amount of items from a block.", BlockItemdroppingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        BlockState state = event.getBlockState();
        List<Item> drops = event.getItems();

        if (drops.isEmpty()) {
            handle(player, qPlayer, state, null, 0);
        }

        for (int i = 0; i < drops.size(); i++) {
            handle(player, qPlayer, state, drops.get(i).getItemStack(), i);
        }
    }

    private void handle(Player player, QPlayer qPlayer, BlockState state, ItemStack item, int i) {
        for (TaskUtils.PendingTask<BlockItemdroppingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BlockItemdroppingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player dropped item from block", quest.getId(), task.getId(), player.getUniqueId());

            final int amountToIncrease;

            if (task.countDrops) {
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

            if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, state, player.getUniqueId())) {
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
