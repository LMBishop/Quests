package me.fatpigsarefat.quests.quests.tasktypes.types;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.blocktype.Block;
import me.fatpigsarefat.quests.blocktype.SimilarBlocks;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import me.fatpigsarefat.quests.quests.tasktypes.ConfigValue;
import me.fatpigsarefat.quests.quests.tasktypes.TaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public final class BuildingCertainTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public BuildingCertainTaskType() {
        super("blockplacecertain", "fatpigsarefat", "Place a set amount of a specific block.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be placed."));
        this.creatorConfigValues.add(new ConfigValue("block", true, "Name or ID of block."));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data code for block."));
        this.creatorConfigValues.add(new ConfigValue("use-similar-blocks", false, "If true, this will ignore orientation of doors, logs etc."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
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
