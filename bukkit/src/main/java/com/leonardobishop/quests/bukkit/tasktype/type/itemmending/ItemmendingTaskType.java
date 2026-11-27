package com.leonardobishop.quests.bukkit.tasktype.type.itemmending;

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
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;

public final class ItemmendingTaskType extends BukkitTaskType<ItemmendingTask> {

    private final BukkitQuestsPlugin plugin;

    public ItemmendingTaskType(BukkitQuestsPlugin plugin) {
        super("itemmending", TaskUtils.TASK_ATTRIBUTION_STRING, "Mend certain items with a set amount of repair.", ItemmendingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemMendEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        ItemStack item = event.getItem();
        int repairAmount = event.getRepairAmount();

        for (TaskUtils.PendingTask<ItemmendingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            ItemmendingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (task.itemRequirement != null) {
                super.debug("Player mended item of type " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

                if (!task.itemRequirement.matches(item)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, repairAmount);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
