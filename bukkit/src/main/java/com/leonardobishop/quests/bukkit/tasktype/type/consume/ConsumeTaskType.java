package com.leonardobishop.quests.bukkit.tasktype.type.consume;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public final class ConsumeTaskType extends BukkitTaskType<ConsumeTask> {

    private static final ItemStack CAKE_ITEM = new ItemStack(Material.CAKE);

    private final BukkitQuestsPlugin plugin;

    public ConsumeTaskType(BukkitQuestsPlugin plugin) {
        super("consume", TaskUtils.TASK_ATTRIBUTION_STRING, "Consume a specific item.", ConsumeTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        this.handle(event.getPlayer(), event.getItem());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        if (!plugin.getVersionSpecificHandler().isCake(type)) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        this.handle(player, CAKE_ITEM);
    }

    public void handle(Player player, ItemStack item) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<ConsumeTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            ConsumeTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (task.item != null) {
                super.debug("Player consumed item of type " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

                if (!task.item.compareItemStack(item, task.exactMatch)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
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
