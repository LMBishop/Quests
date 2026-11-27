package com.leonardobishop.quests.bukkit.tasktype.type.bartering;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * TODO: There is Paper PR to make obtaining the thrower UUID easier: <a href="https://github.com/PaperMC/Paper/pull/5736">Paper#5736</a>
 */
public final class BarteringTaskType extends BukkitTaskType<BarteringTask> {

    private final BukkitQuestsPlugin plugin;
    private final Map<Piglin, UUID> piglin2ThrowerIdMap = new WeakHashMap<>();

    public BarteringTaskType(final @NotNull BukkitQuestsPlugin plugin) {
        super("bartering", TaskUtils.TASK_ATTRIBUTION_STRING, "Make a bartering interaction with a piglin.", BarteringTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractAtEntityEvent(final @NotNull PlayerInteractAtEntityEvent event) {
        final Entity entity = event.getRightClicked();

        if (!(entity instanceof final Piglin piglin && piglin.isAdult())) {
            return;
        }

        final Boolean admiringDisabled = piglin.getMemory(MemoryKey.ADMIRING_DISABLED);

        if (Boolean.TRUE.equals(admiringDisabled)) {
            return;
        }

        final Boolean admiringItem = piglin.getMemory(MemoryKey.ADMIRING_ITEM);

        if (Boolean.TRUE.equals(admiringItem)) {
            return;
        }

        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItem(event.getHand());
        final Material itemType = item.getType();

        if (itemType == Material.GOLD_INGOT || piglin.getBarterList().contains(itemType)) {
            this.piglin2ThrowerIdMap.put(piglin, player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(final @NotNull EntityPickupItemEvent event) {
        final LivingEntity entity = event.getEntity();

        if (entity instanceof final Piglin piglin) {
            final UUID throwerId = event.getItem().getThrower();

            if (throwerId != null) {
                this.piglin2ThrowerIdMap.put(piglin, throwerId);
            } else {
                this.piglin2ThrowerIdMap.remove(piglin);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPiglinBarter(final @NotNull PiglinBarterEvent event) {
        final Piglin piglin = event.getEntity();

        final UUID throwerId = this.piglin2ThrowerIdMap.get(piglin);
        if (throwerId == null) {
            return;
        }

        final Player player = this.plugin.getServer().getPlayer(throwerId);
        if (player == null) {
            return;
        }

        final QPlayer qPlayer = this.plugin.getPlayerManager().getPlayer(throwerId);
        if (qPlayer == null) {
            return;
        }

        final List<ItemStack> outcome = event.getOutcome();
        for (final ItemStack output : outcome) {
            this.handle(player, qPlayer, event.getInput(), output);
        }
    }

    private void handle(final @NotNull Player player, final @NotNull QPlayer qPlayer, final @NotNull ItemStack input, final @NotNull ItemStack output) {
        final int inputAmount = input.getAmount();
        final int outputAmount = output.getAmount();

        for (final TaskUtils.PendingTask<BarteringTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            final Quest quest = pendingTask.quest();
            final BarteringTask task = pendingTask.task();
            final TaskProgress taskProgress = pendingTask.taskProgress();

            this.debug("Player completed a bartering interaction from " + inputAmount + " x " + input.getType() + " to " + outputAmount + " x " + output.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (task.inputRequirement != null) {
                if (!task.inputRequirement.matches(input)) {
                    this.debug("Input does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            if (task.outputRequirement != null) {
                if (!task.outputRequirement.matches(output)) {
                    this.debug("Output does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            final int itemAmount = switch (task.mode) {
                case INPUT -> inputAmount;
                case OUTPUT -> outputAmount;
            };

            final int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, itemAmount);
            this.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                this.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
