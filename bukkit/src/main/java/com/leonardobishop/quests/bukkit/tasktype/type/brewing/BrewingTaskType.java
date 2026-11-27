package com.leonardobishop.quests.bukkit.tasktype.type.brewing;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
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

public final class BrewingTaskType extends BukkitTaskType<BrewingTask> {

    private final BukkitQuestsPlugin plugin;
    private final HashMap<Location, UUID> brewingStands = new HashMap<>();

    public BrewingTaskType(BukkitQuestsPlugin plugin) {
        super("brewing", TaskUtils.TASK_ATTRIBUTION_STRING, "Brew a potion using specific ingredient.", BrewingTask.SCHEMA);
        this.plugin = plugin;
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

        for (TaskUtils.PendingTask<BrewingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BrewingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            if (task.ingredientRequirement != null) {
                super.debug("Player brewed " + eventAmount + " potions" + (ingredient != null ? " using " + ingredient.getType() : ""), quest.getId(), task.getId(), player.getUniqueId());

                if (!task.ingredientRequirement.matches(ingredient)) {
                    super.debug("Ingredient does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, eventAmount);
            super.debug("Updating task progress (now " + (progress + eventAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if ((int) taskProgress.getProgress() >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(task.amount);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
