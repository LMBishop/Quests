package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public final class MiningTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MiningTaskType(BukkitQuestsPlugin plugin) {
        super("blockbreak", TaskUtils.TASK_ATTRIBUTION_STRING, "Break a set amount of a block.", "blockbreakcertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, TaskUtils.MaterialListConfigValidatorMode.BLOCK, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "check-coreprotect"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "check-coreprotect-time"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "reverse-if-placed"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-silk-touch"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();
        Material material = block.getType();
        ItemStack item = plugin.getVersionSpecificHandler().getItemInMainHand(player);
        boolean silkTouchPresent = item != null && item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0;

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player mined block " + material.name(), quest.getId(), task.getId(), event.getPlayer().getUniqueId());

            boolean allowSilkTouch = (boolean) task.getConfigValue("allow-silk-touch", true);
            if (!allowSilkTouch && silkTouchPresent) {
                continue;
            }

            super.debug("allow-silk-touch is disabled, checking block", quest.getId(), task.getId(), player.getUniqueId());

            if (TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                boolean coreProtectEnabled = (boolean) task.getConfigValue("check-coreprotect", false);
                int coreProtectTime = (int) task.getConfigValue("check-coreprotect-time", 3600);

                Runnable increment = () -> {
                    int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                    super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                    int amount = (int) task.getConfigValue("amount");
                    if (progress >= amount) {
                        super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                        taskProgress.setCompleted(true);
                    }
                };

                if (coreProtectEnabled) {
                    AbstractCoreProtectHook coreProtectHook = plugin.getCoreProtectHook();
                    if (coreProtectHook != null) {
                        super.debug("Running CoreProtect lookup (may take a while)", quest.getId(), task.getId(), player.getUniqueId());

                        // Run CoreProtect lookup
                        plugin.getCoreProtectHook().checkBlock(block, coreProtectTime).thenAccept(result -> {
                            if (result) {
                                super.debug("CoreProtect lookup indicates this is a player placed block, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                            } else {
                                super.debug("CoreProtect lookup OK", quest.getId(), task.getId(), player.getUniqueId());
                                increment.run();
                            }
                        }).exceptionally(throwable -> {
                            super.debug("CoreProtect lookup failed: " + throwable.getMessage(), quest.getId(), task.getId(), player.getUniqueId());
                            throwable.printStackTrace();
                            return null;
                        });

                        continue;
                    }

                    super.debug("check-coreprotect is enabled, but CoreProtect is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                }

                increment.run();
            }
        }
    }

    // subtract if enabled
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();
        Material material = block.getType();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player placed block " + material.name(), quest.getId(), task.getId(), player.getUniqueId());

            boolean reverseIfPlaced = (boolean) task.getConfigValue("reverse-if-placed", false);
            if (!reverseIfPlaced) {
                continue;
            }

            super.debug("reverse-if-placed is enabled, checking block", quest.getId(), task.getId(), player.getUniqueId());

            if (TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                int progress = TaskUtils.decrementIntegerTaskProgress(taskProgress);
                super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());
            }
        }
    }
}
