package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.ess3.api.IEssentials;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public final class EssentialsBalanceTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public EssentialsBalanceTaskType(BukkitQuestsPlugin plugin) {
        super("essentials_balance", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a set amount of money.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @Override
    public void onStart(final @NotNull Quest quest, final @NotNull Task task, final @NotNull UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline() || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        IEssentials ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (ess == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
        TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

        BigDecimal balance = ess.getUser(player).getMoney();
        super.debug("Player balance updated to " + balance, quest.getId(), task.getId(), player.getUniqueId());

        taskProgress.setProgress(balance);
        super.debug("Updating task progress (now " + balance + ")", quest.getId(), task.getId(), player.getUniqueId());

        int earningsNeeded = (int) task.getConfigValue("amount");
        BigDecimal amount = BigDecimal.valueOf(earningsNeeded);
        if (balance.compareTo(amount) > 0) {
            super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
            taskProgress.setCompleted(true);
        }
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

            taskProgress.setProgress(balance);
            super.debug("Updating task progress (now " + balance + ")", quest.getId(), task.getId(), player.getUniqueId());

            int earningsNeeded = (int) task.getConfigValue("amount");
            BigDecimal amount = BigDecimal.valueOf(earningsNeeded);
            if (balance.compareTo(amount) > 0) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, earningsNeeded);
        }
    }
}
