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
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(materialName), root + "." + source));
                }
            }
        }
        TaskUtils.configValidateBoolean(root + ".reverse-if-broken", config.get("reverse-if-broken"), problems, true,"reverse-if-broken");
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

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(event.getPlayer(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (matchBlock(task, event.getBlock())) {
                        boolean coreProtectEnabled = (boolean) task.getConfigValue("check-coreprotect", false);
                        int coreProtectTime = (int) task.getConfigValue("check-coreprotect-time", 3600);

                        if (coreProtectEnabled && plugin.getCoreProtectHook().checkBlock(event.getBlock(), coreProtectTime)) {
                            continue;
                        }
                        increment(task, taskProgress, 1);
                    }
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

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (task.getConfigValue("reverse-if-placed") != null && ((boolean) task.getConfigValue("reverse-if-placed"))) {
                        if (matchBlock(task, event.getBlock())) {
                            increment(task, taskProgress, -1);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean matchBlock(Task task, Block block) {
        Material material;

        Object configBlock = task.getConfigValues().containsKey("block") ? task.getConfigValue("block") : task.getConfigValue("blocks");
        Object configData = task.getConfigValue("data");
        Object configSimilarBlocks = task.getConfigValue("use-similar-blocks");

        List<String> checkBlocks = new ArrayList<>();
        if (configBlock instanceof List) {
            checkBlocks.addAll((List) configBlock);
        } else {
            checkBlocks.add(String.valueOf(configBlock));
        }

        for (String materialName : checkBlocks) {
            // LOG:1 LOG:2 LOG should all be supported with this
            String[] split = materialName.split(":");
            int comparableData = 0;
            if (configData != null) {
                comparableData = (int) configData;
            }
            if (split.length > 1) {
                comparableData = Integer.parseInt(split[1]);
            }

            material = Material.getMaterial(String.valueOf(split[0]));
            Material blockType = block.getType();

            short blockData = block.getData();

            if (blockType == material) {
            	if (((split.length == 1 && configData == null) || ((int) blockData) == comparableData))
                	return true;
            }
        }
        return false;
    }

    private void increment(Task task, TaskProgress taskProgress, int amount) {
        int brokenBlocksNeeded = (int) task.getConfigValue("amount");

        int progressBlocksBroken;
        if (taskProgress.getProgress() == null) {
            progressBlocksBroken = 0;
        } else {
            progressBlocksBroken = (int) taskProgress.getProgress();
        }

        taskProgress.setProgress(progressBlocksBroken + amount);

        if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) {
            taskProgress.setCompleted(true);
        }
    }

}
