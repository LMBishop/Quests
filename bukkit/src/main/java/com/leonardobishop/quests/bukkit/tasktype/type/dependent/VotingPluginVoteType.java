package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.bencodez.votingplugin.events.PlayerVoteEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VotingPluginVoteType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public VotingPluginVoteType(BukkitQuestsPlugin plugin) {
        super("votingplugin_vote", TaskUtils.TASK_ATTRIBUTION_STRING, "Vote a set amount of times.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
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

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int votesNeeded = (int) task.getConfigValue("amount");

                    int progressVotes;
                    if (taskProgress.getProgress() == null) {
                        progressVotes = 0;
                    } else {
                        progressVotes = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(votesNeeded + 1);

                    if (((int) taskProgress.getProgress()) >= votesNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
