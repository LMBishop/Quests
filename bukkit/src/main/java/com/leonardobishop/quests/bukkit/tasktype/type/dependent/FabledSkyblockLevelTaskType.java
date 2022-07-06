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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "level"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "level"));
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
//        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
//            TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, false, false, "level");
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

            Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
                Quest quest = pendingTask.quest();
                Task task = pendingTask.task();
                TaskProgress taskProgress = pendingTask.taskProgress();

                int islandLevelNeeded = (int) task.getConfigValue("level");

                super.debug("Player island level updated to " + event.getLevel().getLevel(), quest.getId(), task.getId(), member);

                taskProgress.setProgress(event.getLevel().getLevel());
                super.debug("Updating task progress (now " + event.getLevel().getLevel() + ")", quest.getId(), task.getId(), member);

                if (event.getLevel().getLevel() >= islandLevelNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setProgress(islandLevelNeeded);
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

}
