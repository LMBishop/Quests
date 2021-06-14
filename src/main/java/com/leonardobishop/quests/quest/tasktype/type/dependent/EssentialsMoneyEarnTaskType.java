package com.leonardobishop.quests.quest.tasktype.type.dependent;

import com.leonardobishop.quests.util.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.quest.tasktype.ConfigValue;
import com.leonardobishop.quests.quest.tasktype.TaskType;
import com.leonardobishop.quests.quest.tasktype.TaskUtils;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EssentialsMoneyEarnTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public EssentialsMoneyEarnTaskType() {
        super("essentials_moneyearn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of money.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of money to earn."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, false, "amount");
        return problems;
    }


    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMoneyEarn(UserBalanceUpdateEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int earningsNeeded = (int) task.getConfigValue("amount");

                    BigDecimal current = (BigDecimal) taskProgress.getProgress();
                    if (current == null) {
                        current = new BigDecimal(0);
                    }
                    BigDecimal newProgress = current.add(event.getNewBalance().subtract(event.getOldBalance()));
                    taskProgress.setProgress(newProgress);

                    if (newProgress.compareTo(BigDecimal.valueOf(earningsNeeded)) > 0) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
