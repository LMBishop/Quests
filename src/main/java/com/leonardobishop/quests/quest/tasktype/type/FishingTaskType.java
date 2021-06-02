package com.leonardobishop.quests.quest.tasktype.type;

import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.quest.tasktype.ConfigValue;
import com.leonardobishop.quests.quest.tasktype.TaskType;
import com.leonardobishop.quests.quest.tasktype.TaskUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FishingTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public FishingTaskType() {
        super("fishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of items from the sea.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of fish to be caught."));
        this.creatorConfigValues.add(new ConfigValue("worlds", false, "Permitted worlds the player must be in."));
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
    public void onFishCaught(PlayerFishEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

//        Location hookLocation = event.getHook().getLocation().add(0, -1, 0);
//        if (!(hookLocation.getBlock().getType() == Material.WATER)) {
//            return;
//        }
        
        Player player = event.getPlayer();

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int catchesNeeded = (int) task.getConfigValue("amount");

                    int progressCatches;
                    if (taskProgress.getProgress() == null) {
                        progressCatches = 0;
                    } else {
                        progressCatches = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressCatches + 1);

                    if (((int) taskProgress.getProgress()) >= catchesNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
