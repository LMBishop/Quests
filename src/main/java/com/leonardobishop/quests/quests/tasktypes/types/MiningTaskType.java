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
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MiningTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MiningTaskType() {
        // type, author, description
        super("blockbreak", "LMBishop", "Break a set amount of blocks.");

        // config values for the quest creator to use, if unspecified then the quest creator will not know what to put here (and will require users to
        // go into the config and manually configure there)
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of blocks to be broken."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;  // citizens also causes these events to fire

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer().getUniqueId(), true); // get the qplayer so you can get their progress
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile(); // the quest progress file stores progress about all quests and tasks

        for (Quest quest : super.getRegisteredQuests()) { // iterate through all quests which are registered to use this task type
            if (questProgressFile.hasStartedQuest(quest)) { // check if the player has actually started the quest before progressing it
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest); // get their progress for the specific quest

                for (Task task : quest.getTasksOfType(super.getType())) { // get all tasks of this type
                    if (!TaskUtils.validateWorld(event.getPlayer(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId()); // get the task progress and increment progress by 1

                    if (taskProgress.isCompleted()) { // dont need to increment a completed task
                        continue;
                    }

                    int brokenBlocksNeeded = (int) task.getConfigValue("amount"); // this will retrieve a value from the config under the key "value"

                    int progressBlocksBroken;
                    if (taskProgress.getProgress() == null) { // note: if the player has never progressed before, getProgress() will return null
                        progressBlocksBroken = 0;
                    } else {
                        progressBlocksBroken = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressBlocksBroken + 1); // the progress does not have to be an int, although must be serializable by the yaml provider

                    if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) { // completion statement, if true the task is complete
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
