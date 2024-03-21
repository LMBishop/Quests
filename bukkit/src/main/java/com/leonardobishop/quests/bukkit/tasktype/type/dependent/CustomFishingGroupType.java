package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public final class CustomFishingGroupType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CustomFishingGroupType(BukkitQuestsPlugin plugin) {
        super("customfishing_group", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of loots in a certain group");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "group", "groups"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));

        plugin.getServer().getPluginManager().registerEvents(new CustomFishingListener(), plugin);
    }

    private final class CustomFishingListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onFishing(FishingResultEvent event) {
            handle(event);
        }
    }

    private void handle(FishingResultEvent event) {
        if (event.getResult() == FishingResultEvent.Result.FAILURE)
            return;

        final Player player = event.getPlayer();
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Loot loot = event.getLoot();
        if (loot == null) {
            return;
        }

        String[] groups = loot.getLootGroup();
        if (groups == null || groups.length == 0) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player caught group " + Arrays.toString(groups), quest.getId(), task.getId(), player.getUniqueId());

            loop: {
                for (String group : groups) {
                    if (TaskUtils.matchString(this, pendingTask, group, player.getUniqueId(), "group", "groups", false, false)) {
                        break loop;
                    }
                }
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
