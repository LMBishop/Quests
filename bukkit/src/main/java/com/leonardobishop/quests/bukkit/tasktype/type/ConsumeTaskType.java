package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ConsumeTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public ConsumeTaskType(BukkitQuestsPlugin plugin) {
        super("consume", TaskUtils.TASK_ATTRIBUTION_STRING, "Consume a specific item.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType()))
            TaskUtils.configValidateItemStack(root + ".item", config.get("item"), problems, false, "item");
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true, false, "data");
        return problems;
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (!player.isOnline()) return;

        if (player.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (qPlayer == null) return;

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    Material material;
                    int amount = (int) task.getConfigValue("amount");
                    Object configBlock = task.getConfigValue("item");
                    Object configData = task.getConfigValue("data");

                    QuestItem qi;
                    if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                        if (configBlock instanceof ConfigurationSection) {
                            qi = plugin.getConfiguredQuestItem("", (ConfigurationSection) configBlock);
                        } else {
                            material = Material.getMaterial(String.valueOf(configBlock));
                            ItemStack is;
                            if (material == null) {
                                continue;
                            }
                            if (configData != null) {
                                is = new ItemStack(material, 1, ((Integer) configData).shortValue());
                            } else {
                                is = new ItemStack(material, 1);
                            }
                            qi = new ParsedQuestItem("parsed", null, is);
                        }
                        fixedQuestItemCache.put(quest.getId(), task.getId(), qi);
                    }

                    if (!qi.compareItemStack(event.getItem())) continue;

                    int progress;
                    if (taskProgress.getProgress() == null) {
                        progress = 0;
                    } else {
                        progress = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progress + 1);

                    if ((int) taskProgress.getProgress() >= amount) {
                        taskProgress.setProgress(amount);
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
