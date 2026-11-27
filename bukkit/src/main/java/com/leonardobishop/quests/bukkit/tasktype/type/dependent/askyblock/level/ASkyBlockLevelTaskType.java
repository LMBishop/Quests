package com.leonardobishop.quests.bukkit.tasktype.type.dependent.askyblock.level;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class ASkyBlockLevelTaskType extends BukkitTaskType<ASkyBlockLevelTask> {

    private final BukkitQuestsPlugin plugin;

    public ASkyBlockLevelTaskType(BukkitQuestsPlugin plugin) {
        super("askyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for ASkyBlock.", ASkyBlockLevelTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandPostLevelEvent event) {
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
        if (qPlayer == null) {
            return;
        }

        Player player = Bukkit.getPlayer(event.getPlayer());

        for (TaskUtils.PendingTask<ASkyBlockLevelTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            ASkyBlockLevelTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player island level updated to " + event.getLongLevel(), quest.getId(), task.getId(), event.getPlayer());

            taskProgress.setProgress(event.getLongLevel());
            super.debug("Updating task progress (now " + event.getLongLevel() + ")", quest.getId(), task.getId(), event.getPlayer());

            if (event.getLongLevel() >= task.level) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress((long) task.level);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.level);
        }
    }
}
