package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public final class MiningCertainTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MiningCertainTaskType() {
        super("blockbreakcertain", "LMBishop", "Break a set amount of a specific block.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be broken."));
        this.creatorConfigValues.add(new ConfigValue("block", true, "Name or ID of block."));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data code for block."));
        this.creatorConfigValues.add(new ConfigValue("reverse-if-placed", false, "Will reverse progression if block of same type is placed."));
        this.creatorConfigValues.add(new ConfigValue("use-similar-blocks", false, "(Deprecated) If true, this will ignore orientation of doors, logs etc."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

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
        Object configBlock = task.getConfigValue("block");
        Object configData = task.getConfigValue("data");
        Object configSimilarBlocks = task.getConfigValue("use-similar-blocks");

        material = Material.valueOf(String.valueOf(configBlock));

        Material blockType = block.getType();
        short blockData = block.getData();

        if (blockType == material) {
            return configData == null || blockData == (int) configData;
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

        if ((int) taskProgress.getProgress() >= brokenBlocksNeeded) {
            taskProgress.setCompleted(true);
        }
    }

}
