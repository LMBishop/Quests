package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.bgsoftware.superiorskyblock.api.events.IslandWorthUpdateEvent;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SuperiorSkyblockLevelType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public SuperiorSkyblockLevelType(BukkitQuestsPlugin plugin) {
        super("superiorskyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for SuperiorSkyblock.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
            TaskUtils.configValidateNumber(root + ".level", config.get("level"), problems, false, false, "level");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandWorthUpdateEvent event) {
        for (SuperiorPlayer player : event.getIsland().getIslandMembers(true)) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                continue;
            }

            for (Quest quest : super.getRegisteredQuests()) {
                if (qPlayer.hasStartedQuest(quest)) {
                    QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                    for (Task task : quest.getTasksOfType(super.getType())) {
                        TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                        if (taskProgress.isCompleted()) {
                            continue;
                        }

                        double islandLevelNeeded = Double.parseDouble(String.valueOf(task.getConfigValue("level")));
                        BigDecimal bd = new BigDecimal(islandLevelNeeded);

                        taskProgress.setProgress(event.getNewLevel().doubleValue());

                        if (event.getNewLevel().compareTo(bd) > 0) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }
}
