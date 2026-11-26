package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.finalkill;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class BedWars1058FinalKillTaskType extends BukkitTaskType<BedWars1058FinalKillTask> {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058FinalKillTaskType(BukkitQuestsPlugin plugin) {
        super("bedwars1058_finalkill", TaskUtils.TASK_ATTRIBUTION_STRING, "Get a final kill in BedWars1058.", BedWars1058FinalKillTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerKillEvent event) {
        PlayerKillEvent.PlayerKillCause cause = event.getCause();
        if (!cause.isFinalKill()) {
            return;
        }

        Player killer = event.getKiller();
        if (killer == null || killer.hasMetadata("NPC")) {
            return;
        }

        QPlayer qKiller = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qKiller == null) {
            return;
        }

        for (TaskUtils.PendingTask<BedWars1058FinalKillTask> pendingTask : TaskUtils.getApplicableTasks(killer, qKiller, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BedWars1058FinalKillTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player got a final kill", quest.getId(), task.getId(), killer.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(killer, quest, task, pendingTask, task.amount);
        }
    }
}
