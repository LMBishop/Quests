package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Arrays.asList(
                "break",
                "harvest"
        ), "mode"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        List<Block> brokenBlocks = new ArrayList<>();
        brokenBlocks.add(block);

        boolean performAgeCheck = true;

        if (type == Material.BAMBOO || type == Material.CACTUS || type == Material.KELP || type == Material.SUGAR_CANE) {
            performAgeCheck = false;

            Block anotherBlock = block.getRelative(BlockFace.UP);

            while (true) {
                Material anotherType = anotherBlock.getType();

                if (anotherType == type) {
                    brokenBlocks.add(anotherBlock);
                } else {
                    break;
                }

                anotherBlock = anotherBlock.getRelative(BlockFace.UP);
            }
        }

        handle(event.getPlayer(), brokenBlocks, "break", performAgeCheck);
    }

    private final class HarvestBlockListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onHarvestBlock(org.bukkit.event.player.PlayerHarvestBlockEvent event) {
            handle(event.getPlayer(), event.getHarvestedBlock(), "harvest", true);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void handle(Player player, Block block, String mode, boolean performAgeCheck) {
        handle(player, Collections.singletonList(block), mode, performAgeCheck);
    }

    private void handle(Player player, List<Block> blocks, String mode, boolean performAgeCheck) {
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

    private void handle(Player player, QPlayer qPlayer, Block block, String mode, boolean performAgeCheck) {
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

            String requiredMode = (String) task.getConfigValue("mode");
            if (requiredMode != null && !mode.equals(requiredMode)) {
                super.debug("Mode does not match the required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
            }

            if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");
            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
