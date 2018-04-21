package me.fatpigsarefat.quests.quests.tasktypes.types;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import me.fatpigsarefat.quests.quests.tasktypes.ConfigValue;
import me.fatpigsarefat.quests.quests.tasktypes.TaskType;
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
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockBreakEvent event) {
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

                    if (StringUtils.isNumeric(String.valueOf(configBlock))) {
                        material = Material.getMaterial((int) configBlock);
                    } else {
                        material = Material.getMaterial(String.valueOf(configBlock));
                    }

                    if (material != null && event.getBlock().getType().equals(material)) {
                        if (configData != null && (((int) event.getBlock().getData()) != ((int) configData))) {
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
