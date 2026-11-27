package com.leonardobishop.quests.bukkit.tasktype.type.dependent.votingplugin.vote;

import com.bencodez.votingplugin.events.PlayerVoteEvent;
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

public final class VotingPluginVoteTaskType extends BukkitTaskType<VotingPluginVoteTask> {

    private final BukkitQuestsPlugin plugin;

    public VotingPluginVoteTaskType(BukkitQuestsPlugin plugin) {
        super("votingplugin_vote", TaskUtils.TASK_ATTRIBUTION_STRING, "Vote a set amount of times.", VotingPluginVoteTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVote(PlayerVoteEvent event) {
        String voter = event.getPlayer();
        Player player = Bukkit.getPlayer(voter);

        if (player == null) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask<VotingPluginVoteTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            VotingPluginVoteTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player voted", quest.getId(), task.getId(), player.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
