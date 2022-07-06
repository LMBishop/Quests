package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

public final class ExpEarnTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ExpEarnTaskType(BukkitQuestsPlugin plugin) {
        super("expearn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of exp.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpEarn(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();

        if (player.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            int amountEarned = e.getAmount();

            super.debug("Player earned " + amountEarned + " XP", quest.getId(), task.getId(), player.getUniqueId());

            int expNeeded = (int) task.getConfigValue("amount");

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
            int newProgress = progress + amountEarned;
            taskProgress.setProgress(newProgress);
            super.debug("Updating task progress (now " + (newProgress) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (newProgress >= expNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(newProgress);
                taskProgress.setCompleted(true);
            }
        }
    }
}
