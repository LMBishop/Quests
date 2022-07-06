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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class InventoryTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public InventoryTaskType(BukkitQuestsPlugin plugin) {
        super("inventory", TaskUtils.TASK_ATTRIBUTION_STRING, "Obtain a set of items.");
        this.plugin = plugin;

        try {
            Class.forName("org.bukkit.event.player.PlayerBucketEntityEvent");
            plugin.getServer().getPluginManager().registerEvents(new BucketEntityListener(), plugin);
        } catch (ClassNotFoundException ignored) { } // server version cannot support event
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType()))
            TaskUtils.configValidateItemStack(root + ".item", config.get("item"), problems, false, "item");
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true, false, "data");
        TaskUtils.configValidateBoolean(root + ".remove-items-when-complete", config.get("remove-items-when-complete"), problems, true, "remove-items-when-complete", super.getType());
        TaskUtils.configValidateBoolean(root + ".update-progress", config.get("update-progress"), problems, true, "update-progress", super.getType());
        TaskUtils.configValidateBoolean(root + ".allow-partial-completion", config.get("allow-partial-completion"), problems, true, "allow-partial-completion", super.getType());
        return problems;
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> this.checkInventory(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory((Player) event.getPlayer()), 1L); //Still some work to do as it doesn't really work
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory(event.getPlayer()), 1L);
    }

    private final class BucketEntityListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onBucketEntity(org.bukkit.event.player.PlayerBucketEntityEvent event) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory(event.getPlayer()), 1L);
        }
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Inventory check triggered", quest.getId(), task.getId(), player.getUniqueId());

            int itemsNeeded = (int) task.getConfigValue("amount");
            boolean remove = TaskUtils.getConfigBoolean(task, "remove-items-when-complete");
            boolean allowPartial = TaskUtils.getConfigBoolean(task, "allow-partial-completion", true);

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);

            int total;
            int[] amountPerSlot = TaskUtils.getAmountsPerSlot(player, qi);
            super.debug("Player has " + amountPerSlot[36] + " of the required item", quest.getId(), task.getId(), player.getUniqueId());

            if (allowPartial) {
                total = Math.min(amountPerSlot[36], itemsNeeded - progress);

                if (total == 0) {
                    continue;
                }

                // We must ALWAYS remove items if partial completion is allowed
                // https://github.com/LMBishop/Quests/issues/375
                TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());

                progress += total;
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + (progress) + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= itemsNeeded) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                }
            } else {
                total = Math.min(amountPerSlot[36], itemsNeeded);

                taskProgress.setProgress(total);
                if (total >= itemsNeeded) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());

                    if (remove) {
                        TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                        super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }
        }
    }

}