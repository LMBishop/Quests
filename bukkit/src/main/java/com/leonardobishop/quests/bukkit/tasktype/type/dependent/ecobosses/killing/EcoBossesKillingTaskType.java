package com.leonardobishop.quests.bukkit.tasktype.type.dependent.ecobosses.killing;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.LivingEcoBoss;
import com.willfp.ecobosses.events.BossKillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class EcoBossesKillingTaskType extends BukkitTaskType<EcoBossesKillingTask> {

    private final BukkitQuestsPlugin plugin;

    public EcoBossesKillingTaskType(BukkitQuestsPlugin plugin) {
        super("ecobosses_killing", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of an EcoBosses entity.", EcoBossesKillingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBossKill(BossKillEvent event) {
        Player player = event.getKiller();
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        LivingEcoBoss boss = event.getBoss();
        EcoBoss ecoBoss = boss.getBoss();

        for (TaskUtils.PendingTask<EcoBossesKillingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            EcoBossesKillingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed EcoBosses boss '" + ecoBoss.getDisplayName() + "' (id = " + ecoBoss.getID() + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, task.ids, task.idMatchMode, ecoBoss.getID(), player.getUniqueId(), false, false)) {
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
