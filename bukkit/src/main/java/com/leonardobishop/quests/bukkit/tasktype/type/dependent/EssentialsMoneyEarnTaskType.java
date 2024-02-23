package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;

public final class EssentialsMoneyEarnTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public EssentialsMoneyEarnTaskType(BukkitQuestsPlugin plugin) {
        super("essentials_moneyearn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of money.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUserBalanceUpdate(UserBalanceUpdateEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            BigDecimal balance = event.getNewBalance();
            super.debug("Player balance updated to " + balance, quest.getId(), task.getId(), player.getUniqueId());

            Object progress = taskProgress.getProgress();
            BigDecimal current;
            if (progress instanceof Double d) {
                current = BigDecimal.valueOf(d);
            } else if (progress != null) {
                current = (BigDecimal) progress;
            } else {
                current = new BigDecimal(0);
            }

            BigDecimal oldBalance = event.getOldBalance();
            BigDecimal difference = balance.subtract(oldBalance);
            BigDecimal newProgress = current.add(difference);

            taskProgress.setProgress(newProgress);
            super.debug("Updating task progress (now " + newProgress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int earningsNeeded = (int) task.getConfigValue("amount");
            BigDecimal amount = BigDecimal.valueOf(earningsNeeded);
            if (newProgress.compareTo(amount) > 0) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, earningsNeeded);
        }
    }
}
