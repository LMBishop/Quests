package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class BrewingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final HashMap<Location, UUID> brewingStands = new HashMap<>();
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public BrewingTaskType(BukkitQuestsPlugin plugin) {
        super("brewing", TaskUtils.TASK_ATTRIBUTION_STRING, "Brew a potion using specific ingredient.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "ingredient"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();
        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        final InventoryHolder holder = inventory.getHolder();
        if (holder == null) {
            return;
        }

        brewingStands.put(inventory.getLocation(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        UUID uuid;
        if ((uuid = brewingStands.get(event.getBlock().getLocation())) == null) {
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
        if (qPlayer == null) {
            return;
        }

        final ItemStack ingredient = event.getContents().getIngredient();
        final ItemStack[] contents = event.getContents().getContents();
        final List<ItemStack> results = event.getResults();

        int eventAmount = 0;
        for (int i = 0; i < results.size(); i++) {
            if (contents[i] != null && !contents[i].isSimilar(results.get(i))) {
                eventAmount++;
            }
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (task.hasConfigKey("ingredient")) {
                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "ingredient", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                super.debug("Player brewed " + eventAmount + " potions" + (ingredient != null ? " using " + ingredient.getType() : ""), quest.getId(), task.getId(), player.getUniqueId());

                boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
                if (!qi.compareItemStack(ingredient, exactMatch)) {
                    super.debug("Ingredient does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, eventAmount);
            super.debug("Updating task progress (now " + (progress + eventAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if ((int) taskProgress.getProgress() >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(amount);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
