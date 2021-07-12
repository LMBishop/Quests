package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.songoda.skyblock.api.event.island.IslandLevelChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FabledSkyblockLevelTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public FabledSkyblockLevelTaskType(BukkitQuestsPlugin plugin) {
        super("fabledskyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for FabledSkyblock.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
            TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, false, false, "level");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandLevelChangeEvent event) {
        List<UUID> members = new ArrayList<>();
        members.add(event.getIsland().getOwnerUUID());
        members.addAll(event.getIsland().getCoopPlayers().keySet());

        for (UUID member : members) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(member);
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

                        int islandValueNeeded = (int) task.getConfigValue("level");

                        taskProgress.setProgress(event.getLevel().getLevel());

                        if (((double) taskProgress.getProgress()) >= islandValueNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }

    }

}
