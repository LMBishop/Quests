package com.leonardobishop.quests.bukkit.tasktype.type.dependent.uskyblock.level;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import us.talabrek.ultimateskyblock.api.event.uSkyBlockScoreChangedEvent;

public final class uSkyBlockLevelTaskType extends BukkitTaskType<uSkyBlockLevelTask> {

    private final BukkitQuestsPlugin plugin;

    public uSkyBlockLevelTaskType(BukkitQuestsPlugin plugin) {
        super("uskyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for uSkyBlock.", uSkyBlockLevelTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(uSkyBlockScoreChangedEvent event) {
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask<uSkyBlockLevelTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            uSkyBlockLevelTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player island level updated to " + event.getScore().getScore(), quest.getId(), task.getId(), player.getUniqueId());

            taskProgress.setProgress(event.getScore().getScore());
            super.debug("Updating task progress (now " + event.getScore().getScore() + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (event.getScore().getScore() >= task.level) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress((long) task.level);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.level);
        }
    }
}
