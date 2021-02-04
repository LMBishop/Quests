package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.earth2me.essentials.Essentials;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EssentialsBalanceTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public EssentialsBalanceTaskType() {
        super("essentials_balance", "LMBishop", "Reach a set amount of money.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of money to reach."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, false, "amount");
        return problems;
    }

    @Override
    public void onStart(Quest quest, Task task, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (player != null && player.isOnline() && ess != null) {
            QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(playerUUID);
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

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMoneyEarn(UserBalanceUpdateEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId(), true);
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int earningsNeeded = (int) task.getConfigValue("amount");

                    taskProgress.setProgress(event.getNewBalance());

                    if (event.getNewBalance().compareTo(BigDecimal.valueOf(earningsNeeded)) > 0) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
