package com.leonardobishop.quests.bukkit.tasktype.type;

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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class CommandTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CommandTaskType(BukkitQuestsPlugin plugin) {
        super("command", TaskUtils.TASK_ATTRIBUTION_STRING, "Execute a certain command.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "command"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "ignore-case"));
        super.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "command-match-mode"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        String message = event.getMessage();
        if (!message.isEmpty()) {
            message = message.substring(1);
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player sent command '/" + message + "'", quest.getId(), task.getId(), player.getUniqueId());

            boolean ignoreCase = TaskUtils.getConfigBoolean(task, "ignore-case");

            if (!TaskUtils.matchString(this, pendingTask, message, player.getUniqueId(), "command", "commands", false, "command-match-mode", ignoreCase)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
            taskProgress.setCompleted(true);
        }
    }
}
