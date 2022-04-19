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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SmeltingCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public SmeltingCertainTaskType(BukkitQuestsPlugin plugin) {
        super("smeltingcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Smelt or cook a set amount of certain item.");
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
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        InventoryType inventoryType = event.getInventory().getType();

        if (event.getRawSlot() != 2 || !plugin.getVersionSpecificHandler().isFurnaceInventoryType(inventoryType)
                || item == null || item.getType() == Material.AIR || event.getAction() == InventoryAction.NOTHING
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR && cursor != null && cursor.getAmount() == cursor.getMaxStackSize()
                || event.getClick() == ClickType.NUMBER_KEY && event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
                || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        int eventAmount = item.getAmount();
        if (event.isShiftClick()) {
            eventAmount = Math.min(eventAmount, plugin.getVersionSpecificHandler().getAvailableSpace(player, item));
            if (eventAmount == 0) {
                return;
            }
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (task.getConfigValue("mode") != null
                            && !inventoryType.toString().equalsIgnoreCase(task.getConfigValue("mode").toString())) {
                        continue;
                    }

                    QuestItem qi;
                    Object configItem = task.getConfigValue("item");
                    Object configData = task.getConfigValue("data");

                    if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                        if (configItem instanceof ConfigurationSection) {
                            qi = plugin.getConfiguredQuestItem("", (ConfigurationSection) configItem);
                        } else {
                            Material material = Material.getMaterial(String.valueOf(configItem));
                            if (material == null) {
                                continue;
                            }

                            ItemStack is;
                            if (configData != null) {
                                is = new ItemStack(material, 1, ((Integer) configData).shortValue());
                            } else {
                                is = new ItemStack(material, 1);
                            }
                            qi = new ParsedQuestItem("parsed", null, is);
                        }
                        fixedQuestItemCache.put(quest.getId(), task.getId(), qi);
                    }

                    if (qi.compareItemStack(item)) {
                        int smeltedItemsNeeded = (int) task.getConfigValue("amount");

                        int progressItemsSmelted;
                        if (taskProgress.getProgress() == null) {
                            progressItemsSmelted = 0;
                        } else {
                            progressItemsSmelted = (int) taskProgress.getProgress();
                        }

                        taskProgress.setProgress(progressItemsSmelted + eventAmount);

                        if (((int) taskProgress.getProgress()) >= smeltedItemsNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }

}
