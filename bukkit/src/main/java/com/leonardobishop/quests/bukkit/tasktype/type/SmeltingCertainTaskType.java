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

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
//        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType()))
//            TaskUtils.configValidateItemStack(root + ".item", config.get("item"), problems, false, "item");
//        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
//            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
//        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true, false, "data");
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
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player smelted item", quest.getId(), task.getId(), player.getUniqueId());

            if (task.getConfigValue("mode") != null
                    && !inventoryType.toString().equalsIgnoreCase(task.getConfigValue("mode").toString())) {
                super.debug("Specific mode is required, but the actual mode '" + inventoryType + "' does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            if (qi.compareItemStack(item)) {
                int smeltedItemsNeeded = (int) task.getConfigValue("amount");

                int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                int newAmount = progress + eventAmount;
                taskProgress.setProgress(newAmount);
                super.debug("Updating task progress (now " + (newAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (newAmount >= smeltedItemsNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setProgress(newAmount);
                    taskProgress.setCompleted(true);
                }
            } else {
                super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
            }
        }
    }

}
