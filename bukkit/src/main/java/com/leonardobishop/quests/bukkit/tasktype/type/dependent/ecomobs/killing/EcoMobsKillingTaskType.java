package com.leonardobishop.quests.bukkit.tasktype.type.dependent.ecomobs.killing;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.willfp.ecomobs.event.EcoMobKillEvent;
import com.willfp.ecomobs.mob.EcoMob;
import com.willfp.ecomobs.mob.LivingMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class EcoMobsKillingTaskType extends BukkitTaskType<EcoMobsKillingTask> {

    private final BukkitQuestsPlugin plugin;

    public EcoMobsKillingTaskType(BukkitQuestsPlugin plugin) {
        super("ecomobs_killing", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of an EcoMobs entity.", EcoMobsKillingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEcoMobKill(EcoMobKillEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        LivingMob mob = event.getMob();
        EcoMob ecoMob = mob.getMob();

        for (TaskUtils.PendingTask<EcoMobsKillingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            EcoMobsKillingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed EcoMobs mob '" + mob.getDisplayName() + "' (id = " + mob.getMob().getID() + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, task.ids, task.idMatchMode, ecoMob.getID(), player.getUniqueId(), false, false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

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
