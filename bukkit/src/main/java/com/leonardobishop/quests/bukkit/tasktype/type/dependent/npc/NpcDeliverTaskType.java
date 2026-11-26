package com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.Player;

public abstract class NpcDeliverTaskType<T> extends BukkitTaskType<NpcDeliverTask<T>> {

    public NpcDeliverTaskType(String type, String author, String description, TaskSchema<NpcDeliverTask<T>> schema) {
        super(type, author, description, schema);
    }

    public void checkInventory(Player player, T npcId, String npcName, long delay, BukkitQuestsPlugin plugin) {
        if (player.hasMetadata("NPC") || !player.isOnline()) return;
        plugin.getScheduler().runTaskLaterAtLocation(player.getLocation(), () -> checkInventory(player, npcId, npcName, plugin), delay);
    }

    public void checkInventory(Player player, T npcId, String npcName, BukkitQuestsPlugin plugin) {
        if (!player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        boolean nameCorrected = false;

        for (TaskUtils.PendingTask<NpcDeliverTask<T>> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            NpcDeliverTask<T> task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player clicked NPC", quest.getId(), task.getId(), player.getUniqueId());

            if (task.npcIds != null) {
                if (!task.npcIds.contains(npcId)) {
                    super.debug("NPC id " + npcId + " does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
                if (task.npcName != null) {
                    if (npcName == null) {
                        super.debug("NPC name is empty and does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    if (!nameCorrected) {
                        npcName = Chat.legacyStrip(Chat.legacyColor(npcName));
                        nameCorrected = true;
                    }

                    if (!task.npcName.equals(npcName)) {
                        super.debug("NPC name " + npcName + " does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }
                }
            }

            int[] amountPerSlot = TaskUtils.getAmountsPerSlot(player, task.item, task.exactMatch);
            super.debug("Player has " + amountPerSlot[36] + " of the required item", quest.getId(), task.getId(), player.getUniqueId());

            if (task.allowPartialCompletion) {
                int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                int total = Math.min(amountPerSlot[36], task.amount - progress);

                if (total == 0) {
                    continue;
                }

                // We must ALWAYS remove items if partial completion is allowed
                // https://github.com/LMBishop/Quests/issues/375
                TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                super.debug("Removing " + total + " items from inventory", quest.getId(), task.getId(), player.getUniqueId());

                progress += total;
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= task.amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                }
            } else {
                int progress = Math.min(amountPerSlot[36], task.amount);
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= task.amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());

                    if (task.removeItemsWhenComplete) {
                        TaskUtils.removeItemsInSlots(player, amountPerSlot, progress);
                        super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
