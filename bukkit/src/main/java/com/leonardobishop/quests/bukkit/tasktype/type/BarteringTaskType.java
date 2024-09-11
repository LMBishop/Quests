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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * TODO: There is Paper PR to make obtaining the thrower UUID easier: <a href="https://github.com/PaperMC/Paper/pull/5736">Paper#5736</a>
 */
public final class BarteringTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Map<Piglin, UUID> piglin2ThrowerIdMap = new WeakHashMap<>();
    private final Table<String, String, QuestItem> fixedQuestInputCache = HashBasedTable.create();
    private final Table<String, String, QuestItem> fixedQuestOutputCache = HashBasedTable.create();

    public BarteringTaskType(final @NotNull BukkitQuestsPlugin plugin) {
        super("bartering", TaskUtils.TASK_ATTRIBUTION_STRING, "Make a bartering interaction with a piglin.");
        this.plugin = plugin;

        this.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        this.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        this.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "input"));
        this.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "output"));
        this.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "input-exact-match"));
        this.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "output-exact-match"));
        this.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Mode.STRING_MODE_MAP.keySet(), "mode"));
    }

    @Override
    public void onReady() {
        this.fixedQuestInputCache.clear();
        this.fixedQuestOutputCache.clear();
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

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            final Quest quest = pendingTask.quest();
            final Task task = pendingTask.task();
            final TaskProgress taskProgress = pendingTask.taskProgress();

            this.debug("Player completed a bartering interaction from " + inputAmount + " x " + input.getType() + " to " + outputAmount + " x " + output.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (task.hasConfigKey("input")) {
                QuestItem qi;
                if ((qi = this.fixedQuestInputCache.get(quest.getId(), task.getId())) == null) {
                    final QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "input", "data");
                    this.fixedQuestInputCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                final boolean exactMatch = TaskUtils.getConfigBoolean(task, "input-exact-match", true);
                if (!qi.compareItemStack(input, exactMatch)) {
                    this.debug("Input does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            if (task.hasConfigKey("output")) {
                QuestItem qi;
                if ((qi = this.fixedQuestOutputCache.get(quest.getId(), task.getId())) == null) {
                    final QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "output", "data");
                    this.fixedQuestOutputCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                final boolean exactMatch = TaskUtils.getConfigBoolean(task, "output-exact-match", true);
                if (!qi.compareItemStack(output, exactMatch)) {
                    this.debug("Output does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            final Object modeObject = task.getConfigValue("mode");

            // not suspicious at all ඞ
            //noinspection SuspiciousMethodCalls
            final Mode requiredMode = Mode.STRING_MODE_MAP.getOrDefault(modeObject, Mode.OUTPUT);

            final int itemAmount = switch (requiredMode) {
                case INPUT -> inputAmount;
                case OUTPUT -> outputAmount;
            };

            final int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, itemAmount);
            this.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            final int amount = (int) task.getConfigValue("amount");
            if (progress >= amount) {
                this.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }

    private enum Mode {
        INPUT,
        OUTPUT;

        private static final Map<String, BarteringTaskType.Mode> STRING_MODE_MAP = new HashMap<>() {{
            for (final BarteringTaskType.Mode mode : BarteringTaskType.Mode.values()) {
                this.put(mode.name().toLowerCase(Locale.ROOT), mode);
            }
        }};
    }
}
