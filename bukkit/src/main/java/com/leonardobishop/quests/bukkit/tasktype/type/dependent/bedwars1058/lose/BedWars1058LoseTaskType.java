package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.lose;

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

public final class BedWars1058LoseTaskType extends BukkitTaskType<BedWars1058LoseTask> {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058LoseTaskType(BukkitQuestsPlugin plugin) {
        super("bedwars1058_lose", TaskUtils.TASK_ATTRIBUTION_STRING, "Lose a game of BedWars in BedWars1058.", BedWars1058LoseTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameEnd(GameEndEvent event) {
        List<UUID> loserUniqueIds = event.getLosers();

        for (UUID loserUniqueId : loserUniqueIds) {
            Player loser = Bukkit.getPlayer(loserUniqueId);

            if (loser != null) {
                this.handle(loser);
            }
        }
    }

    private void handle(final Player loser) {
        if (loser.hasMetadata("NPC")) {
            return;
        }

        QPlayer qLoser = plugin.getPlayerManager().getPlayer(loser.getUniqueId());
        if (qLoser == null) {
            return;
        }

        for (TaskUtils.PendingTask<BedWars1058LoseTask> pendingTask : TaskUtils.getApplicableTasks(loser, qLoser, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BedWars1058LoseTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player lost a BedWars game", quest.getId(), task.getId(), loser.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), loser.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), loser.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(loser, quest, task, pendingTask, task.amount);
        }
    }
}
