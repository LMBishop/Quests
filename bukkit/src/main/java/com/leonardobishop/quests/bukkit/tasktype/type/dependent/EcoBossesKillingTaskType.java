package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.events.BossKillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class EcoBossesKillingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public EcoBossesKillingTaskType(BukkitQuestsPlugin plugin) {
        super("ecobosses_killing", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of an EcoBosses entity.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "id", "ids"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBossKill(BossKillEvent event) {
        Player killer = event.getKiller();
        EcoBoss boss = event.getBoss().getBoss();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(killer, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed EcoBosses mob '" + boss.getDisplayName() + "' (id = " + boss.getID() + ")", quest.getId(), task.getId(), killer.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, boss.getID(), killer.getUniqueId(), "id", "ids", false, false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(killer, quest, task, taskProgress, amount);
        }
    }
}
