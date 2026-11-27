package com.leonardobishop.quests.bukkit.tasktype.type.dependent.superiorskyblock.level;

import com.bgsoftware.superiorskyblock.api.events.IslandWorthUpdateEvent;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;

public final class SuperiorSkyblockLevelTaskType extends BukkitTaskType<SuperiorSkyblockLevelTask> {

    private final BukkitQuestsPlugin plugin;

    public SuperiorSkyblockLevelTaskType(BukkitQuestsPlugin plugin) {
        super("superiorskyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for SuperiorSkyblock.", SuperiorSkyblockLevelTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandWorthUpdateEvent event) {
        for (SuperiorPlayer superiorPlayer : event.getIsland().getIslandMembers(true)) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(superiorPlayer.getUniqueId());
            if (qPlayer == null) {
                continue;
            }

            Player player = Bukkit.getPlayer(superiorPlayer.getUniqueId());

            if (player == null) {
                continue;
            }

            for (TaskUtils.PendingTask<SuperiorSkyblockLevelTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
                Quest quest = pendingTask.quest();
                SuperiorSkyblockLevelTask task = pendingTask.task();
                TaskProgress taskProgress = pendingTask.taskProgress();

                super.debug("Player island level updated", quest.getId(), task.getId(), player.getUniqueId());

                BigDecimal bd = new BigDecimal(task.level);

                taskProgress.setProgress(event.getNewLevel().doubleValue());
                super.debug("Updating task progress (now " + event.getNewLevel().doubleValue() + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (event.getNewLevel().compareTo(bd) >= 0) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }

                TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.level);
            }
        }
    }
}
