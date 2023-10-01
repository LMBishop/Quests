package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class DealDamageTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public DealDamageTaskType(BukkitQuestsPlugin plugin) {
        super("dealdamage", TaskUtils.TASK_ATTRIBUTION_STRING, "Deal a certain amount of damage.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-only-creatures"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) {
            return;
        }

        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Entity entity = event.getEntity();
        double damage = event.getDamage();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player damaged " + entity.getType() + " for " + damage, quest.getId(), task.getId(), player.getUniqueId());

            boolean allowOnlyCreatures = TaskUtils.getConfigBoolean(task, "allow-only-creatures", true);
            if (allowOnlyCreatures && !(entity instanceof Creature)) {
                super.debug(entity.getType() + " is not a creature but allow-only-creatures is true, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int amount = (int) task.getConfigValue("amount");
            double progress = Math.min(amount, TaskUtils.getDecimalTaskProgress(taskProgress) + damage);

            taskProgress.setProgress(progress);
            super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
            TaskUtils.sendTrackAdvancement(player, quest, task, taskProgress, amount);
        }
    }
}
