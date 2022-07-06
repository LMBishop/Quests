package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MiningCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MiningCertainTaskType(BukkitQuestsPlugin plugin) {
        super("blockbreakcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Break a set amount of a specific block.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        if (config.get("block") == null && config.get("blocks") == null) {
            TaskUtils.configValidateExists(root + ".block", config.get("block"), problems, "block", super.getType());
        } else {
            Object configBlock;
            String source;
            if (config.containsKey("block")) {
                source = "block";
            } else {
                source = "blocks";
            }
            configBlock = config.get(source);
            List<String> checkBlocks = new ArrayList<>();
            if (configBlock instanceof List) {
                checkBlocks.addAll((List) configBlock);
            } else {
                checkBlocks.add(String.valueOf(configBlock));
            }

            for (String materialName : checkBlocks) {
                String[] split = materialName.split(":");
                if (Material.getMaterial(String.valueOf(split[0])) == null) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(materialName),
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(materialName),
                            root + "." + source));
                }
            }
        }
        TaskUtils.configValidateBoolean(root + ".reverse-if-placed", config.get("reverse-if-placed"), problems, true,"reverse-if-placed");
        TaskUtils.configValidateBoolean(root + ".check-coreprotect", config.get("check-coreprotect"), problems, true,"check-coreprotect");
        TaskUtils.configValidateInt(root + ".check-coreprotect-time", config.get("check-coreprotect-time"), problems, true,true, "check-coreprotect-time");
        TaskUtils.configValidateBoolean(root + ".use-similar-blocks", config.get("use-similar-blocks"), problems, true,"use-similar-blocks");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true,true, "data");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(event.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player mined block " + event.getBlock().getType(), quest.getId(), task.getId(), event.getPlayer().getUniqueId());

            if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                boolean coreProtectEnabled = (boolean) task.getConfigValue("check-coreprotect", false);
                int coreProtectTime = (int) task.getConfigValue("check-coreprotect-time", 3600);

                if (coreProtectEnabled && plugin.getCoreProtectHook() == null) {
                    super.debug("check-coreprotect is enabled, but CoreProtect is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                }

                if (plugin.getCoreProtectHook() != null && plugin.getCoreProtectHook().checkBlock(event.getBlock(), coreProtectTime)) {
                    super.debug("CoreProtect indicates blocks was recently placed, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                int blocksNeeded = (int) task.getConfigValue("amount");

                if (progress >= blocksNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

    // subtract if enabled
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(event.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player placed block " + event.getBlock().getType(), quest.getId(), task.getId(), event.getPlayer().getUniqueId());


            if (task.getConfigValue("reverse-if-placed") != null && ((boolean) task.getConfigValue("reverse-if-placed"))) {
                super.debug("reverse-if-placed is enabled, checking block", quest.getId(), task.getId(), event.getPlayer().getUniqueId());
                if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                    int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                    taskProgress.setProgress(--progress);
                    super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());
                }
            }
        }
    }

}
