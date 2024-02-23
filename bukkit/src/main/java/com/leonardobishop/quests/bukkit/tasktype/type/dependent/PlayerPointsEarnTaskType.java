package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.UUID;

public final class PlayerPointsEarnTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public PlayerPointsEarnTaskType(BukkitQuestsPlugin plugin) {
        super("playerpoints_earn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of points.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPointsChange(PlayerPointsChangeEvent event) {
        UUID playerId = event.getPlayerId();
        int change = event.getChange();

        Player player = Bukkit.getPlayer(playerId);
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(playerId);
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player points changed: " + change, quest.getId(), task.getId(), playerId);

            int amount = (int) task.getConfigValue("amount");

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
            int newProgress = change != 0 ? progress + change : 0;
            taskProgress.setProgress(newProgress);
            super.debug("Updating task progress (now " + newProgress + ")", quest.getId(), task.getId(), playerId);

            if (newProgress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), playerId);
                taskProgress.setProgress(amount);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
