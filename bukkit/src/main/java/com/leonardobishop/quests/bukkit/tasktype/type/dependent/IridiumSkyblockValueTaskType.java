package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.api.IslandWorthCalculatedEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//TODO update to latest ver
public final class IridiumSkyblockValueTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public IridiumSkyblockValueTaskType(BukkitQuestsPlugin plugin) {
        super("iridiumskyblock_value", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island value for Iridium Skyblock.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
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
                continue;
            }
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
            if (qPlayer == null) {
                continue;
            }

            for (Quest quest : IridiumSkyblockValueTaskType.super.getRegisteredQuests()) {
                if (qPlayer.hasStartedQuest(quest)) {
                    QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                    for (Task task : quest.getTasksOfType(IridiumSkyblockValueTaskType.super.getType())) {
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

}
