package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class BedWars1058WinTask extends BukkitTaskType implements Listener {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058WinTask(BukkitQuestsPlugin plugin) {
        super("bedwars1058_win", TaskUtils.TASK_ATTRIBUTION_STRING, "Win a game of BedWars in BedWars1058.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameEnd(GameEndEvent event) {

        for (UUID uuid : event.getWinners()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                return;
            }

            for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
                Quest quest = pendingTask.quest();
                Task task = pendingTask.task();
                TaskProgress taskProgress = pendingTask.taskProgress();

                super.debug("Player won a BedWars game", quest.getId(), task.getId(), player.getUniqueId());

                Runnable increment = () -> {
                    int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                    super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                    int amount = (int) task.getConfigValue("amount");
                    if (progress >= amount) {
                        super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                        taskProgress.setCompleted(true);
                    }

                    TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
                };

                increment.run();
            }
        }
    }
}
