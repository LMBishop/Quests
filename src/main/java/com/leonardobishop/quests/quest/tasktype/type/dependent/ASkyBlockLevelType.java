package com.leonardobishop.quests.quest.tasktype.type.dependent;

import com.leonardobishop.quests.util.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.quest.tasktype.ConfigValue;
import com.leonardobishop.quests.quest.tasktype.TaskType;
import com.leonardobishop.quests.quest.tasktype.TaskUtils;
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ASkyBlockLevelType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public ASkyBlockLevelType() {
        super("askyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for ASkyBlock.");
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
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getPlayer());
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
