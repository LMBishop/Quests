package com.leonardobishop.quests.bukkit.tasktype.type.expearn;

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
import org.bukkit.event.player.PlayerExpChangeEvent;

public final class ExpEarnTaskType extends BukkitTaskType<ExpEarnTask> {

    private final BukkitQuestsPlugin plugin;

    public ExpEarnTaskType(BukkitQuestsPlugin plugin) {
        super("expearn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of exp.", ExpEarnTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpEarn(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();

        if (player.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<ExpEarnTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            ExpEarnTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            int amountEarned = e.getAmount();

            super.debug("Player earned " + amountEarned + " XP", quest.getId(), task.getId(), player.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, amountEarned);
            super.debug("Updating task progress (now " + (progress) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(task.amount);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
