package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.win;

import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.UUID;

public final class BedWars1058WinTaskType extends BukkitTaskType<BedWars1058WinTask> {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058WinTaskType(BukkitQuestsPlugin plugin) {
        super("bedwars1058_win", TaskUtils.TASK_ATTRIBUTION_STRING, "Win a game of BedWars in BedWars1058.", BedWars1058WinTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameEnd(GameEndEvent event) {
        List<UUID> winnerUniqueIds = event.getLosers();

        for (UUID winnerUniqueId : winnerUniqueIds) {
            Player winner = Bukkit.getPlayer(winnerUniqueId);

            if (winner != null) {
                this.handle(winner);
            }
        }
    }

    private void handle(final Player winner) {
        if (winner.hasMetadata("NPC")) {
            return;
        }

        QPlayer qWinner = plugin.getPlayerManager().getPlayer(winner.getUniqueId());
        if (qWinner == null) {
            return;
        }

        for (TaskUtils.PendingTask<BedWars1058WinTask> pendingTask : TaskUtils.getApplicableTasks(winner, qWinner, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BedWars1058WinTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player won a BedWars game", quest.getId(), task.getId(), winner.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), winner.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), winner.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(winner, quest, task, pendingTask, task.amount);
        }
    }
}
