package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.cmi.AbstractCMIHook;
import com.leonardobishop.quests.bukkit.hook.essentials.AbstractEssentialsHook;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class PlaytimeTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private WrappedTask poll;

    public PlaytimeTaskType(BukkitQuestsPlugin plugin) {
        super("playtime", TaskUtils.TASK_ATTRIBUTION_STRING, "Track the amount of playing time a user has been on");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "ignore-afk"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "minutes"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "minutes"));
    }

    @Override
    public void onReady() {
        if (this.poll != null) {
            return;
        }

        this.poll = new WrappedRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handle(player);
                }
            }
        }.runTaskTimer(plugin.getScheduler(), 1200L, 1200L);
    }

    private void handle(Player player) {
        if (player.hasMetadata("NPC")) {
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

            super.debug("Polling playtime for player", quest.getId(), task.getId(), player.getUniqueId());

            boolean ignoreAfk = TaskUtils.getConfigBoolean(task, "ignore-afk", false);

            if (ignoreAfk) {
                super.debug("ignore-afk is enabled, checking hooks...", quest.getId(), task.getId(), player.getUniqueId());

                AbstractCMIHook cmiHook = plugin.getCMIHook();
                if (cmiHook != null && cmiHook.isAfk(player)) {
                    super.debug("CMI reports player as afk, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                AbstractEssentialsHook essentialsHook = plugin.getEssentialsHook();
                if (essentialsHook != null && essentialsHook.isAfk(player)) {
                    super.debug("Essentials reports player as afk, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                if (cmiHook == null && essentialsHook == null) {
                    super.debug("ignore-afk is enabled, but no hooks found, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int minutes = (int) task.getConfigValue("minutes");
            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= minutes) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, minutes);
        }
    }

    @Override
    public void onDisable() {
//        if (this.poll != null) {
//            this.poll.cancel();
//        }
    }

    @Override
    public @NonNull Object getGoal(final @NonNull Task task) {
        return task.getConfigValue("minutes", "-");
    }
}
