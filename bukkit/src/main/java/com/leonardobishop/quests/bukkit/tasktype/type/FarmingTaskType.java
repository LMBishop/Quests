package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, "block", "blocks"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        handle(event.getPlayer(), event.getBlock(), "break");
    }

    private final class HarvestBlockListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onHarvestBlock(org.bukkit.event.player.PlayerHarvestBlockEvent event) {
            handle(event.getPlayer(), event.getHarvestedBlock(), "harvest");
        }
    }

    private void handle(Player player, Block block, String mode) {
        if (!(block.getState().getBlockData() instanceof Ageable)) {
            return;
        }

        Ageable crop = (Ageable) block.getState().getBlockData();
        if (crop.getAge() != crop.getMaximumAge()) {
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

            super.debug("Player farmed crop " + crop.getMaterial() + " (mode = " + mode + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (task.getConfigValue("mode") != null
                    && !mode.equalsIgnoreCase(task.getConfigValue("mode").toString())) {
                super.debug("Mode does not match required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (matchBlock(task, quest, player, block)) {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                int amount = (int) task.getConfigValue("amount");

                if (progress >= amount) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

    private boolean matchBlock(Task task, Quest quest, Player player, Block block) {
        Material material;

        List<String> checkBlocks = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("block") ? "block" : "blocks");
        if (checkBlocks.isEmpty()) {
            return true;
        }

        for (String materialName : checkBlocks) {
            super.debug("Checking against '" + materialName + "'", quest.getId(), task.getId(), player.getUniqueId());
            material = Material.getMaterial(String.valueOf(materialName));
            Material blockType = block.getType();

            if (blockType == material) {
                super.debug("Type match", quest.getId(), task.getId(), player.getUniqueId());
                return true;
            } else {
                super.debug("Type mismatch", quest.getId(), task.getId(), player.getUniqueId());
            }
        }
        return false;
    }

}