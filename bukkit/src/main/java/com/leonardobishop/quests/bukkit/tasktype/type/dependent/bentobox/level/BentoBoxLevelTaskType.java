package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bentobox.level;

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
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.level.events.IslandLevelCalculatedEvent;

import java.util.Set;
import java.util.UUID;

public final class BentoBoxLevelTaskType extends BukkitTaskType<BentoBoxLevelTask> {

    private final BukkitQuestsPlugin plugin;

    public BentoBoxLevelTaskType(final @NotNull BukkitQuestsPlugin plugin) {
        super("bentobox_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level in the level addon for BentoBox.", BentoBoxLevelTask.SCHEMA);
        this.plugin = plugin;

    }

    @EventHandler
    public void onIslandLevelCalculated(final @NotNull IslandLevelCalculatedEvent event) {
        final Island island = event.getIsland();
        final Set<UUID> memberIds = island.getMemberSet();
        final long level = event.getLevel();

        for (final UUID memberId : memberIds) {
            final Player player = Bukkit.getPlayer(memberId);

            if (player != null) {
                this.handle(player, level);
            }
        }
    }

    private void handle(final @NotNull Player player, final long level) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        final QPlayer qPlayer = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (final TaskUtils.PendingTask<BentoBoxLevelTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            final Quest quest = pendingTask.quest();
            final BentoBoxLevelTask task = pendingTask.task();
            final TaskProgress taskProgress = pendingTask.taskProgress();

            this.debug("Player island level updated to " + level, quest.getId(), task.getId(), player.getUniqueId());

            //noinspection DataFlowIssue // TODO quest data rework

            final long clampedLevel = Math.min(level, task.level);
            taskProgress.setProgress(clampedLevel);
            this.debug("Updating task progress (now " + clampedLevel + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (level >= task.level) {
                this.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.level);
        }
    }
}
