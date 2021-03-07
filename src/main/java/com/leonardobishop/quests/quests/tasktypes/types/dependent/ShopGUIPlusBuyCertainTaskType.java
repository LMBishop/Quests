package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import net.brcdev.shopgui.event.ShopPreTransactionEvent;
import net.brcdev.shopgui.shop.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopGUIPlusBuyCertainTaskType extends TaskType {

    public ShopGUIPlusBuyCertainTaskType() {
        super("shopguiplus_buycertain", "LMBishop", "Purchase a given item from a ShopGUI+ shop");
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, false, "amount");
        TaskUtils.configValidateExists(root + ".id", config.get("id"), problems, "id", super.getType());
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(ShopPreTransactionEvent event) {
        if (event.getShopAction() != ShopManager.ShopAction.BUY) return;

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(event.getPlayer().getWorld().getName(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    String configName = (String) task.getConfigValue("id");

                    if (!event.getShopItem().getId().equals(configName)) {
                        return;
                    }

                    int amountNeeded = (int) task.getConfigValue("amount");

                    int progressAmount;
                    if (taskProgress.getProgress() == null) {
                        progressAmount = 0;
                    } else {
                        progressAmount = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressAmount + event.getAmount());

                    if (((int) taskProgress.getProgress()) >= amountNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }
}
