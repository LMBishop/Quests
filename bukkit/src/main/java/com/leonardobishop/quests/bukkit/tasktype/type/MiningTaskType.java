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

public final class MiningTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MiningTaskType(BukkitQuestsPlugin plugin) {
        super("blockbreak", TaskUtils.TASK_ATTRIBUTION_STRING, "Break a set amount of a block.", "blockbreakcertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "check-coreprotect"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "check-coreprotect-time"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "reverse-if-placed"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "use-similar-blocks"));
    }

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

            if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                boolean coreProtectEnabled = (boolean) task.getConfigValue("check-coreprotect", false);
                int coreProtectTime = (int) task.getConfigValue("check-coreprotect-time", 3600);

                if (coreProtectEnabled && plugin.getCoreProtectHook() == null) {
                    super.debug("check-coreprotect is enabled, but CoreProtect is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                }

                if (plugin.getCoreProtectHook() != null && plugin.getCoreProtectHook().checkBlock(event.getBlock(), coreProtectTime)) {
                    super.debug("CoreProtect indicates blocks was recently placed, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

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


            if (task.getConfigValue("reverse-if-placed") != null && ((boolean) task.getConfigValue("reverse-if-placed"))) {
                super.debug("reverse-if-placed is enabled, checking block", quest.getId(), task.getId(), event.getPlayer().getUniqueId());
                if (TaskUtils.matchBlock(this, pendingTask, event.getBlock(), player.getUniqueId())) {
                    int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                    taskProgress.setProgress(--progress);
                    super.debug("Decrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());
                }
            }
        }
    }

}
