package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.playerblocktracker.AbstractPlayerBlockTrackerHook;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FarmingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public FarmingTaskType(BukkitQuestsPlugin plugin) {
        super("farming", TaskUtils.TASK_ATTRIBUTION_STRING, "Break or harvest a set amount of a crop.", "farmingcertain");
        this.plugin = plugin;

        try {
            Class.forName("org.bukkit.event.player.PlayerHarvestBlockEvent");
            plugin.getServer().getPluginManager().registerEvents(new FarmingTaskType.HarvestBlockListener(), plugin);
        } catch (ClassNotFoundException ignored) { } // server version cannot support event

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, TaskUtils.MaterialListConfigValidatorMode.BLOCK, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "check-playerblocktracker"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "check-coreprotect"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "check-coreprotect-time"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Mode.STRING_MODE_MAP.keySet(), "mode"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        List<Block> brokenBlocks = new ArrayList<>();
        brokenBlocks.add(block);

        boolean performAgeCheck = true;

        if (type == Material.BAMBOO || type == Material.CACTUS || type == Material.KELP || type == Material.KELP_PLANT || type == Material.SUGAR_CANE) {
            performAgeCheck = false;

            Block anotherBlock = block.getRelative(BlockFace.UP);

            while (true) {
                Material anotherType = anotherBlock.getType();

                // We need a way more elegant solution to check the kelp thing
                if (anotherType == type || (type == Material.KELP_PLANT && anotherType == Material.KELP)) {
                    brokenBlocks.add(anotherBlock);
                } else {
                    break;
                }

                anotherBlock = anotherBlock.getRelative(BlockFace.UP);
            }
        }

        handle(event.getPlayer(), brokenBlocks, Mode.BREAK, performAgeCheck);
    }

    private final class HarvestBlockListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onHarvestBlock(org.bukkit.event.player.PlayerHarvestBlockEvent event) {
            handle(event.getPlayer(), event.getHarvestedBlock(), Mode.HARVEST, true);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void handle(Player player, Block block, Mode mode, boolean performAgeCheck) {
        handle(player, Collections.singletonList(block), mode, performAgeCheck);
    }

    private void handle(Player player, List<Block> blocks, Mode mode, boolean performAgeCheck) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Block block : blocks) {
            handle(player, qPlayer, block, mode, performAgeCheck);
        }
    }

    private void handle(Player player, QPlayer qPlayer, Block block, Mode mode, boolean performAgeCheck) {
        if (performAgeCheck) {
            BlockData blockData = block.getBlockData();
            if (!(blockData instanceof Ageable crop && crop.getAge() == crop.getMaximumAge() || plugin.getVersionSpecificHandler().isCaveVinesPlantWithBerries(blockData))) {
                return;
            }
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player farmed a crop " + block.getType() + " (mode = " + mode + ")", quest.getId(), task.getId(), player.getUniqueId());

            Object requiredModeObject = task.getConfigValue("mode");

            // not suspicious at all ඞ
            //noinspection SuspiciousMethodCalls
            Mode requiredMode = Mode.STRING_MODE_MAP.get(requiredModeObject);

            if (requiredMode != null && mode != requiredMode) {
                super.debug("Mode does not match the required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            boolean playerBlockTrackerEnabled = TaskUtils.getConfigBoolean(task, "check-playerblocktracker");

            if (playerBlockTrackerEnabled) {
                AbstractPlayerBlockTrackerHook playerBlockTrackerHook = plugin.getPlayerBlockTrackerHook();
                if (playerBlockTrackerHook != null) {
                    super.debug("Running PlayerBlockTracker lookup", quest.getId(), task.getId(), player.getUniqueId());

                    boolean result = playerBlockTrackerHook.checkBlock(block);
                    if (result) {
                        super.debug("PlayerBlockTracker lookup indicates this is a player placed block, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    super.debug("PlayerBlockTracker lookup OK", quest.getId(), task.getId(), player.getUniqueId());
                } else {
                    super.debug("check-playerblocktracker is enabled, but PlayerBlockTracker is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                    continue; // we want to prevent progressing in quest if PBT failed to start and was expected to
                }
            }

            Runnable increment = () -> {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                int amount = (int) task.getConfigValue("amount");
                if (progress >= amount) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }

                TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
            };

            boolean coreProtectEnabled = TaskUtils.getConfigBoolean(task, "check-coreprotect");
            int coreProtectTime = (int) task.getConfigValue("check-coreprotect-time", 3600);

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
                continue; // we want to prevent progressing in quest if CoreProtect failed to start and was expected to
            }

            increment.run();
        }
    }

    private enum Mode {
        BREAK,
        HARVEST;

        private static final Map<String, Mode> STRING_MODE_MAP = new HashMap<>() {{
            for (final Mode mode : Mode.values()) {
                this.put(mode.name().toLowerCase(Locale.ROOT), mode);
            }
        }};
    }
}
