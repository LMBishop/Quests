package com.leonardobishop.quests.bukkit.tasktype.type.interact;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class InteractTaskType extends BukkitTaskType<InteractTask> {

    private final BukkitQuestsPlugin plugin;

    public InteractTaskType(BukkitQuestsPlugin plugin) {
        super("interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with an item a certain amount of times.", InteractTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        EquipmentSlot hand = plugin.getVersionSpecificHandler().getHand(event);
        Event.Result useInteractedBlock = event.useInteractedBlock();
        Event.Result useItemInHand = event.useItemInHand();

        for (TaskUtils.PendingTask<InteractTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            InteractTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player interacted with " + action + " using " + hand + " resulting in block " + useInteractedBlock + " and item " + useItemInHand, quest.getId(), task.getId(), player.getUniqueId());

            // keep ignoreCancelled default behaviour
            boolean effectivelyCancelled = useInteractedBlock == Event.Result.DENY &&
                    task.useInteractedBlockResults == null;
            if (effectivelyCancelled) {
                super.debug("Continuing... (event is effectively cancelled)", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchEnum(this, pendingTask, task.useInteractedBlockResults, useInteractedBlock, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchEnum(this, pendingTask, task.useItemInHandResults, useItemInHand, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchEnum(this, pendingTask, task.actions, action, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchEnum(this, pendingTask, task.hands, hand, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchBlock(this, pendingTask, task.blocks, task.data, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.itemRequirement != null) {
                if (item == null) {
                    super.debug("Specific item is required, player has no item in hand; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                super.debug("Specific item is required; player held item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                if (!task.itemRequirement.matches(item)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
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
