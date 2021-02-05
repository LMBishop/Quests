package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.api.IslandWorthCalculatedEvent;
import com.leonardobishop.quests.Quests;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class IridiumSkyblockValueType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public IridiumSkyblockValueType() {
        super("iridiumskyblock_value", "LMBishop", "Reach a certain island value for Iridium Skyblock.");
        this.creatorConfigValues.add(new ConfigValue("value", true, "Minimum island value needed."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".value", config.get("value"), problems, "value", super.getType()))
            TaskUtils.configValidateInt(root + ".value", config.get("value"), problems, false, false, "value");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandWorthCalculatedEvent event) {
        Island island = event.getIsland();
        for (String player : island.members) {
            UUID uuid;
            try {
                 uuid = UUID.fromString(player);
            } catch (Exception e) {
                Quests.get().getQuestsLogger().debug("Cannot convert from String to UUID for IridiumSkyblock");
                continue;
            }
            QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(uuid, true);
            if (qPlayer == null) {
                continue;
            }

            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

            for (Quest quest : IridiumSkyblockValueType.super.getRegisteredQuests()) {
                if (questProgressFile.hasStartedQuest(quest)) {
                    QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                    for (Task task : quest.getTasksOfType(IridiumSkyblockValueType.super.getType())) {
                        TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                        if (taskProgress.isCompleted()) {
                            continue;
                        }

                        int islandValueNeeded = (int) task.getConfigValue("value");

                        taskProgress.setProgress(event.getIslandWorth());

                        if (((double) taskProgress.getProgress()) >= islandValueNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }

    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

}
