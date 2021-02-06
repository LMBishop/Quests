package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MiningCertainTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MiningCertainTaskType() {
        super("blockbreakcertain", "LMBishop", "Break a set amount of a specific block.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be broken."));
        this.creatorConfigValues.add(new ConfigValue("block", true, "Name or ID of block.", "block")); // Can use name:datacode
        this.creatorConfigValues.add(new ConfigValue("blocks", true, "List of blocks (alias for block for config readability).", "block"));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data code for block.")); // only used if no datacode provided in block or blocks
        this.creatorConfigValues.add(new ConfigValue("reverse-if-placed", false, "Will reverse progression if block of same type is placed."));
        this.creatorConfigValues.add(new ConfigValue("use-similar-blocks", false, "(Deprecated) If true, this will ignore orientation of doors, logs etc."));
        this.creatorConfigValues.add(new ConfigValue("worlds", false, "Permitted worlds the player must be in."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
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
                    problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                            QuestsConfigLoader.ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(materialName), root + "." + source));
                }
            }
        }
        TaskUtils.configValidateBoolean(root + ".reverse-if-broken", config.get("reverse-if-broken"), problems, true,"reverse-if-broken");
        TaskUtils.configValidateBoolean(root + ".use-similar-blocks", config.get("use-similar-blocks"), problems, true,"use-similar-blocks");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true,true, "data");
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (matchBlock(task, event.getBlock())) {
                        increment(task, taskProgress, 1);
                    }
                }
            }
        }
    }

    // subtract if enabled
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(event.getPlayer(), task)) continue;

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
