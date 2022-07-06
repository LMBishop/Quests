package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DealDamageTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public DealDamageTaskType(BukkitQuestsPlugin plugin) {
        super("dealdamage", TaskUtils.TASK_ATTRIBUTION_STRING, "Deal a certain amount of damage.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-only-creatures"));
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
//        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
//            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
//        TaskUtils.configValidateBoolean(root + ".allow-only-creatures", config.get("allow-only-creatures"), problems, true, "allow-only-creatures");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        Entity entity = event.getEntity();
        double damage = event.getDamage();

        if (player.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player damaged " + entity.getType() + " for " + damage, quest.getId(), task.getId(), player.getUniqueId());

            boolean allowOnlyCreatures = TaskUtils.getConfigBoolean(task, "allow-only-creatures", true);
            if (allowOnlyCreatures && !(event.getEntity() instanceof Creature)) {
                super.debug(entity.getType() + " is not a creature but allow-only-creatures is true, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            double progressDamage = TaskUtils.getDecimalTaskProgress(taskProgress);
            int damageNeeded = (int) task.getConfigValue("amount");

            taskProgress.setProgress(progressDamage + damage);
            super.debug("Updating task progress (now " + (progressDamage + damage) + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (((double) taskProgress.getProgress()) >= (double) damageNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(damageNeeded);
                taskProgress.setCompleted(true);
            }
        }
    }
}
