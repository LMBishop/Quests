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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.UUID;

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

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "remove-items-when-complete"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-partial-completion"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @Override
    public void onStart(Quest quest, Task task, UUID playerUUID) {
        checkInventory(Bukkit.getPlayer(playerUUID), 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        checkInventory(event.getPlayer(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        checkInventory(event.getPlayer(), 1L); // Still some work to do as it doesn't really work
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        checkInventory(event.getPlayer(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        checkInventory(event.getPlayer(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        checkInventory(event.getPlayer(), 1L);
    }

    private final class BucketEntityListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onBucketEntity(org.bukkit.event.player.PlayerBucketEntityEvent event) {
            checkInventory(event.getPlayer(), 1L);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void checkInventory(HumanEntity humanEntity, long delay) {
        if (!(humanEntity instanceof Player player)) return;
        checkInventory(player, delay);
    }

    private void checkInventory(Player player, long delay) {
        if (player == null || player.hasMetadata("NPC") || !player.isOnline()) return;
        plugin.getScheduler().runTaskLaterAtLocation(player.getLocation(), () -> checkInventory(player), delay);
    }

    private void checkInventory(Player player) {
        if (!player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Inventory check triggered", quest.getId(), task.getId(), player.getUniqueId());

            boolean allowPartial = TaskUtils.getConfigBoolean(task, "allow-partial-completion");

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
            int[] amountPerSlot = TaskUtils.getAmountsPerSlot(player, qi, exactMatch);
            super.debug("Player has " + amountPerSlot[36] + " of the required item", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (allowPartial) {
                int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                int total = Math.min(amountPerSlot[36], amount - progress);

                if (total == 0) {
                    continue;
                }

                // We must ALWAYS remove items if partial completion is allowed
                // https://github.com/LMBishop/Quests/issues/375
                TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                super.debug("Removing " + total + " items from inventory", quest.getId(), task.getId(), player.getUniqueId());

                progress += total;
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                }
            } else {
                int progress = Math.min(amountPerSlot[36], amount);
                int oldProgress = TaskUtils.getIntegerTaskProgress(taskProgress);

                if (progress == oldProgress) {
                    // no need to update, also no need to check for progress >= amount
                    // as quest completer will handle that properly after some time
                    // we don't want to send track advancement for each inventory op too
                    continue;
                }

                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());

                    boolean remove = TaskUtils.getConfigBoolean(task, "remove-items-when-complete");

                    if (remove) {
                        TaskUtils.removeItemsInSlots(player, amountPerSlot, progress);
                        super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}