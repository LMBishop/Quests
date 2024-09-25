package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class BedWars1058FinalKillTask extends BukkitTaskType implements Listener {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058FinalKillTask(BukkitQuestsPlugin plugin) {
        super("bedwars1058_finalkill", TaskUtils.TASK_ATTRIBUTION_STRING, "Get a final kill in BedWars1058.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerKillEvent event) {
        Player killer = event.getKiller();
        if (killer == null) {
            return;
        }

        if (!event.getCause().isFinalKill()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(killer, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player got a final kill", quest.getId(), task.getId(), killer.getUniqueId());

            Runnable increment = () -> {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

                int amount = (int) task.getConfigValue("amount");
                if (progress >= amount) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                    taskProgress.setCompleted(true);
                }

                TaskUtils.sendTrackAdvancement(killer, quest, task, pendingTask, amount);
            };

            increment.run();
        }
    }
}
