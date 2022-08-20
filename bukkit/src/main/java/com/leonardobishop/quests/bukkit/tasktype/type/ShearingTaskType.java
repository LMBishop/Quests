package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

public final class ShearingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ShearingTaskType(BukkitQuestsPlugin plugin) {
        super("shearing", TaskUtils.TASK_ATTRIBUTION_STRING, "Shear a set amount of sheep.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useDyeColorConfigValidator(this, "color", "colors"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if (!(event.getEntity() instanceof Sheep sheep)) {
            return;
        }

        if (event.getPlayer().hasMetadata("NPC")) return;

        Player player = event.getPlayer();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player sheared animal", quest.getId(), task.getId(), player.getUniqueId());

            if (TaskUtils.matchDyeColor(this, pendingTask, sheep, player.getUniqueId())) {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                int sheepNeeded = (int) task.getConfigValue("amount");

                if (progress >= sheepNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

}
