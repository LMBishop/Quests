package com.leonardobishop.quests.bukkit.tasktype.type.bucket;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class BucketInteractionTaskType extends BukkitTaskType<BucketInteractionTask> {

    protected final BukkitQuestsPlugin plugin;

    public BucketInteractionTaskType(BukkitQuestsPlugin plugin, String type, String author, String description) {
        super(type, author, description, BucketInteractionTask.SCHEMA);
        this.plugin = plugin;
    }

    protected void handle(Player player, ItemStack item) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<BucketInteractionTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BucketInteractionTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (task.bucket != null) {
                super.debug("Player interacted with bucket of type " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

                if (!task.bucket.compareItemStack(item, task.exactMatch)) {
                    super.debug("Item does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
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
