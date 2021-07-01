package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.brcdev.shopgui.event.ShopPreTransactionEvent;
import net.brcdev.shopgui.shop.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopGUIPlusBuyCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ShopGUIPlusBuyCertainTaskType(BukkitQuestsPlugin plugin) {
        super("shopguiplus_buycertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Purchase a given item from a ShopGUI+ shop");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, false, "amount");
        TaskUtils.configValidateExists(root + ".id", config.get("id"), problems, "id", super.getType());
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(ShopPreTransactionEvent event) {
        if (event.getShopAction() != ShopManager.ShopAction.BUY) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
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
