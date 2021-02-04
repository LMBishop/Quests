package com.leonardobishop.quests.quests.tasktypes.types.dependent;

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
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ASkyBlockLevelType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public ASkyBlockLevelType() {
        super("askyblock_level", "LMBishop", "Reach a certain island level for ASkyBlock.");
        this.creatorConfigValues.add(new ConfigValue("level", true, "Minimum island level needed."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
            TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, false, false, "level");
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandPostLevelEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer(), true);
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    long islandLevelNeeded = (long) (int) task.getConfigValue("level");

                    taskProgress.setProgress(event.getLongLevel());

                    if (((long) taskProgress.getProgress()) >= islandLevelNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }
}
