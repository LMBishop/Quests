package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public final class CustomFishingFishingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CustomFishingFishingTaskType(BukkitQuestsPlugin plugin) {
        super("customfishing_fishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of CustomFishing loots");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "loot-match-mode"));
        super.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "group-match-mode"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishingResult(FishingResultEvent event) {
        FishingResultEvent.Result result = event.getResult();
        if (result != FishingResultEvent.Result.SUCCESS) {
            return;
        }

        Loot loot = event.getLoot();
        if (loot == null) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        String id = loot.id();
        String[] groups = loot.lootGroup();

        // Cache for performance reasons (used in debug logging)
        String groupsAsString = Arrays.toString(groups);

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player caught loot id " + id + " groups " + groupsAsString, quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, id, player.getUniqueId(), "loot", "loots", false, "loot-match-mode", false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchAnyString(this, pendingTask, groups, player.getUniqueId(), "group", "groups", false, "group-match-mode", false)) {
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
