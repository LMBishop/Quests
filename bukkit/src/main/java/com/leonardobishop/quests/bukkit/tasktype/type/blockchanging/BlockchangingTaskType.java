package com.leonardobishop.quests.bukkit.tasktype.type.blockchanging;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.playerblocktracker.AbstractPlayerBlockTrackerHook;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jetbrains.annotations.NotNull;

public final class BlockchangingTaskType extends BukkitTaskType<BlockchangingTask> {

    private final BukkitQuestsPlugin plugin;

    public BlockchangingTaskType(final @NotNull BukkitQuestsPlugin plugin) {
        super("blockchanging", TaskUtils.TASK_ATTRIBUTION_STRING, "Change a set amount of certain blocks.", BlockchangingTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
        Entity entity  = event.getEntity();
        if (!(entity instanceof Player player) || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block from = event.getBlock();
        BlockData toData = event.getBlockData();

        for (TaskUtils.PendingTask<BlockchangingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            BlockchangingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player changed a block, from block is " + from.getType() + ", to block is " + event.getTo(), quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchBlock(this, pendingTask, task.froms, task.data, from, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchBlock(this, pendingTask, task.tos, task.data, toData, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.checkPlayerBlockTracker) {
                AbstractPlayerBlockTrackerHook playerBlockTrackerHook = plugin.getPlayerBlockTrackerHook();
                if (playerBlockTrackerHook != null) {
                    super.debug("Running PlayerBlockTracker lookup", quest.getId(), task.getId(), player.getUniqueId());

                    boolean result = playerBlockTrackerHook.checkBlock(from);
                    if (result) {
                        super.debug("PlayerBlockTracker lookup indicates this is a player placed block, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    super.debug("PlayerBlockTracker lookup OK", quest.getId(), task.getId(), player.getUniqueId());
                } else {
                    super.debug("check-playerblocktracker is enabled, but PlayerBlockTracker is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                    continue; // we want to prevent progressing in quest if PBT failed to start and was expected to
                }
            }

            Runnable increment = () -> {
                int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= task.amount) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }

                TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
            };

            if (task.checkCoreProtect) {
                AbstractCoreProtectHook coreProtectHook = plugin.getCoreProtectHook();
                if (coreProtectHook != null) {
                    super.debug("Running CoreProtect lookup (may take a while)", quest.getId(), task.getId(), player.getUniqueId());

                    // Run CoreProtect lookup
                    plugin.getCoreProtectHook().checkBlock(from, task.checkCoreProtectTime).thenAccept(result -> {
                        if (result) {
                            super.debug("CoreProtect lookup indicates this is a player placed block, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        } else {
                            super.debug("CoreProtect lookup OK", quest.getId(), task.getId(), player.getUniqueId());
                            increment.run();
                        }
                    }).exceptionally(throwable -> {
                        super.debug("CoreProtect lookup failed: " + throwable.getMessage(), quest.getId(), task.getId(), player.getUniqueId());
                        throwable.printStackTrace();
                        return null;
                    });

                    continue;
                }

                super.debug("check-coreprotect is enabled, but CoreProtect is not detected on the server", quest.getId(), task.getId(), player.getUniqueId());
                continue; // we want to prevent progressing in quest if CoreProtect failed to start and was expected to
            }

            increment.run();
        }
    }
}
