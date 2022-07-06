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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {
        if (event.getClickedInventory() == null
                || (event.getClickedInventory().getType() != InventoryType.CRAFTING && event.getClickedInventory().getType() != InventoryType.WORKBENCH)
                || event.getSlotType() != InventoryType.SlotType.RESULT
                || event.getCurrentItem() == null
                || event.getAction() == InventoryAction.NOTHING) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        int clickedAmount = getCraftAmount(event);

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
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

            super.debug("Player crafted " + clickedAmount + " of " + clickedItem.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (!qi.compareItemStack(clickedItem)) {
                super.debug("Item does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
            taskProgress.setProgress(progress + clickedAmount);
            super.debug("Updating task progress (now " + (progress + clickedAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if ((int) taskProgress.getProgress() >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(amount);
                taskProgress.setCompleted(true);
            }
        }
    }

    // thanks https://www.spigotmc.org/threads/util-get-the-crafted-item-amount-from-a-craftitemevent.162952/
    private int getCraftAmount(CraftItemEvent e) {
        if(e.isCancelled()) { return 0; }

        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick() && e.getClick() != ClickType.CONTROL_DROP) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : e.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = e.getRecipe().getResult().getAmount() * possibleCreations;

            ItemStack i = e.getRecipe().getResult();

            for(int s = 0; s <= 35; s++) {
                ItemStack test = p.getInventory().getItem(s);
                if(test == null || test.getType() == Material.AIR) {
                    amountCanBeMade+= i.getMaxStackSize();
                    continue;
                }
                if(test.isSimilar(i)) {
                    amountCanBeMade += i.getMaxStackSize() - test.getAmount();
                }
            }

            return Math.min(amountOfItems, amountCanBeMade);
        } else {
            return e.getRecipe().getResult().getAmount();
        }
    }

}
