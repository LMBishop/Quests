package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.blocktype.Block;
import com.leonardobishop.quests.blocktype.SimilarBlocks;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public final class MiningCertainTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MiningCertainTaskType() {
        super("blockbreakcertain", "fatpigsarefat", "Break a set amount of a specific block.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be broken."));
        this.creatorConfigValues.add(new ConfigValue("block", true, "Name or ID of block."));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data code for block."));
        this.creatorConfigValues.add(new ConfigValue("use-similar-blocks", false, "If true, this will ignore orientation of doors, logs etc."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    Material material;
                    Object configBlock = task.getConfigValue("block");
                    Object configData = task.getConfigValue("data");
                    Object configSimilarBlocks = task.getConfigValue("use-similar-blocks");

                    if (StringUtils.isNumeric(String.valueOf(configBlock))) {
                        material = Material.getMaterial((int) configBlock);
                    } else {
                        material = Material.getMaterial(String.valueOf(configBlock));
                    }

                    Material blockType = event.getBlock().getType();
                    short blockData = event.getBlock().getData();

                    if (configSimilarBlocks != null && ((Boolean) configSimilarBlocks)) {
                        Block block;
                        if ((block = SimilarBlocks.getSimilarBlock(new Block(blockType, blockData))) != null) {
                            blockType = block.getMaterial();
                            blockData = block.getData();
                        }
                    }

                    if (blockType.equals(material)) {
                        if (configData != null && (((int) blockData) != ((int) configData))) {
                            continue;
                        }
                        int brokenBlocksNeeded = (int) task.getConfigValue("amount");

                        int progressBlocksBroken;
                        if (taskProgress.getProgress() == null) {
                            progressBlocksBroken = 0;
                        } else {
                            progressBlocksBroken = (int) taskProgress.getProgress();
                        }

                        taskProgress.setProgress(progressBlocksBroken + 1);

                        if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }

}
