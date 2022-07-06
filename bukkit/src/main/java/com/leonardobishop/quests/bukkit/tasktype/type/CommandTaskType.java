package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public final class CommandTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CommandTaskType(BukkitQuestsPlugin plugin) {
        super("command", TaskUtils.TASK_ATTRIBUTION_STRING, "Execute a certain command.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "command"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "ignore-case"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasMetadata("NPC")) return;

        Player player = e.getPlayer();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            boolean ignoreCasing = TaskUtils.getConfigBoolean(task, "ignore-case");
            List<String> commands = TaskUtils.getConfigStringList(task, "command");

            String message = e.getMessage();
            if (message.length() >= 1) {
                message = message.substring(1);
            }

            super.debug("Player sent command '/" + message + "'", quest.getId(), task.getId(), player.getUniqueId());

            for (String command : commands) {
                super.debug("Checking command against '/" + command + "' (ignore case = " + ignoreCasing + ")", quest.getId(), task.getId(), player.getUniqueId());
                if ((ignoreCasing && command.equalsIgnoreCase(message))
                    || (!ignoreCasing && command.equals(message))) {
                    super.debug("Command '/" + message + "' matches task command '" + command + "'", quest.getId(), task.getId(), player.getUniqueId());
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }
}
