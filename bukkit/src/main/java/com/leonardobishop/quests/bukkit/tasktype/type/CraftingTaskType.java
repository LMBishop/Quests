package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public final class CraftingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public CraftingTaskType(BukkitQuestsPlugin plugin) {
        super("crafting", TaskUtils.TASK_ATTRIBUTION_STRING, "Craft a specific item.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));

    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (event.getAction() == InventoryAction.NOTHING
                || event.getCurrentItem() == null
                || event.getAction() == InventoryAction.NOTHING
                || event.getClick() == ClickType.NUMBER_KEY && event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
                || event.getClick() == ClickType.DROP && event.getAction() == InventoryAction.DROP_ONE_SLOT && event.getCursor() != null
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        int eventAmount = item.getAmount();
        if (event.isShiftClick()) {
            int maxAmount = event.getInventory().getMaxStackSize();
            ItemStack[] matrix = event.getInventory().getMatrix();
            for (ItemStack itemStack : matrix) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    int itemStackAmount = itemStack.getAmount();
                    if (itemStackAmount < maxAmount && itemStackAmount > 0) {
                        maxAmount = itemStackAmount;
                    }
                }
            }
            eventAmount *= maxAmount;
            eventAmount = Math.min(eventAmount, plugin.getVersionSpecificHandler().getAvailableSpace(player, item));
            if (eventAmount == 0) {
                return;
            }
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            int amount = (int) task.getConfigValue("amount");

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            super.debug("Player crafted " + eventAmount + " of " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (!qi.compareItemStack(item)) {
                super.debug("Item does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
            taskProgress.setProgress(progress + eventAmount);
            super.debug("Updating task progress (now " + (progress + eventAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if ((int) taskProgress.getProgress() >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(amount);
                taskProgress.setCompleted(true);
            }
        }
    }

}
