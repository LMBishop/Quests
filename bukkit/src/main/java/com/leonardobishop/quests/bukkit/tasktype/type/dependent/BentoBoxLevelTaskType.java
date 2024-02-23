package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

import java.util.UUID;

public final class BentoBoxLevelTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BentoBoxLevelTaskType(BukkitQuestsPlugin plugin) {
        super("bentobox_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level in the level addon for BentoBox.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "level"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "level"));
    }

    public static void register(BukkitQuestsPlugin plugin, TaskTypeManager manager) {
        if (BentoBox.getInstance().getAddonsManager().getAddonByName("Level").isPresent()) {
            manager.registerTaskType(new BentoBoxLevelTaskType(plugin));
        }
    }

    // https://github.com/BentoBoxWorld/bentobox/issues/352
    @EventHandler
    public void onBentoBoxIslandLevelCalculated(IslandBaseEvent event) {
        if ("IslandLevelCalculatedEvent".equals(event.getEventName())) {
            Island island = (Island) event.getKeyValues().get("island");

            for (UUID member : island.getMemberSet()) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(member);
                if (qPlayer == null) {
                    continue;
                }

                Player player = Bukkit.getPlayer(member);

                if (player == null) {
                    continue;
                }

                for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
                    Quest quest = pendingTask.quest();
                    Task task = pendingTask.task();
                    TaskProgress taskProgress = pendingTask.taskProgress();

                    long islandLevelNeeded = (long) (int) task.getConfigValue("level");
                    long newLevel = (long) event.getKeyValues().get("level");

                    super.debug("Player island level updated to " + newLevel, quest.getId(), task.getId(), member);

                    taskProgress.setProgress(newLevel);
                    super.debug("Updating task progress (now " + newLevel + ")", quest.getId(), task.getId(), player.getUniqueId());

                    if (newLevel >= islandLevelNeeded) {
                        super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                        taskProgress.setProgress(newLevel);
                        taskProgress.setCompleted(true);
                    }

                    TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, islandLevelNeeded);
                }
            }
        }
    }
}
