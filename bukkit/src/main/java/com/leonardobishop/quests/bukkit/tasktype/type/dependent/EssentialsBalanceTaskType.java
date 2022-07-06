package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.earth2me.essentials.Essentials;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, false, "amount");
        return problems;
    }

    @Override
    public void onStart(Quest quest, Task task, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (player != null && player.isOnline() && ess != null) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(playerUUID);
            if (qPlayer == null) {
                return;
            }
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
            TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

            int earningsNeeded = (int) task.getConfigValue("amount");
            BigDecimal money = ess.getUser(player).getMoney();
            taskProgress.setProgress(money);
            if (money.compareTo(BigDecimal.valueOf(earningsNeeded)) > 0) {
                taskProgress.setCompleted(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMoneyEarn(UserBalanceUpdateEvent event) {
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player balance updated to " + event.getNewBalance(), quest.getId(), task.getId(), player.getUniqueId());

            int earningsNeeded = (int) task.getConfigValue("amount");

            taskProgress.setProgress(event.getNewBalance());
            super.debug("Updating task progress (now " + event.getNewBalance() + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (event.getNewBalance().compareTo(BigDecimal.valueOf(earningsNeeded)) > 0) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}
