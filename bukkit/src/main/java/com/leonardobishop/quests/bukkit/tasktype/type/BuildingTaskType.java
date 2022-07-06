package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BuildingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BuildingTaskType(BukkitQuestsPlugin plugin) {
        super("blockplace", TaskUtils.TASK_ATTRIBUTION_STRING, "Place a set amount of a block.", "blockplacecertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "reverse-if-broken"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "use-similar-blocks"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(event.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player placed block " + event.getBlock().getType(), quest.getId(), task.getId(), event.getPlayer().getUniqueId());

            if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                int blocksNeeded = (int) task.getConfigValue("amount");

                if (progress >= blocksNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

    // subtract if enabled
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getPlayer();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(event.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player mined block " + event.getBlock().getType(), quest.getId(), task.getId(), event.getPlayer().getUniqueId());


            if (task.getConfigValue("reverse-if-broken") != null && ((boolean) task.getConfigValue("reverse-if-placed"))) {
                super.debug("reverse-if-broken is enabled, checking block", quest.getId(), task.getId(), event.getPlayer().getUniqueId());
                if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                    int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                    taskProgress.setProgress(--progress);
                    super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());
                }
            }
        }
    }


}
