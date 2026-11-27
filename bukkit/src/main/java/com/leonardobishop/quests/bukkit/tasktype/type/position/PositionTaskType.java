package com.leonardobishop.quests.bukkit.tasktype.type.position;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.NumberConversions;

public final class PositionTaskType extends BukkitTaskType<PositionTask> {

    private final BukkitQuestsPlugin plugin;

    public PositionTaskType(BukkitQuestsPlugin plugin) {
        super("position", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a set of co-ordinates.", PositionTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!blockLocationsDiffer(event)) {
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

        Location to = event.getTo();

        for (TaskUtils.PendingTask<PositionTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            PositionTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            if (task.world != null) {
                World world = Bukkit.getWorld(task.world);
                if (world == null) {
                    super.debug("World " + task.world + " does not exist, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else if (!to.getWorld().equals(world)) {
                    super.debug("Specific world is required, but the actual world " + to.getWorld().getName() + " does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            Location location = new Location(null, task.x, task.y, task.z);
            double distanceSquared = distanceSquared(location, to); // use own distanceSquared method to skip world validation

            super.debug("Player is " + distanceSquared + " meters squared away (padding = " + task.distancePadding + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (task.distancePadding != null && (distanceSquared <= task.distancePadding * task.distancePadding)) {
                super.debug("Player is within distance padding", quest.getId(), task.getId(), player.getUniqueId());
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            } else if (!blockLocationsDiffer(location, to, true)) {
                super.debug("Player is precisely at location", quest.getId(), task.getId(), player.getUniqueId());
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

    private boolean blockLocationsDiffer(PlayerMoveEvent event) {
        return blockLocationsDiffer(event.getFrom(), event.getTo(), false);
    }

    private boolean blockLocationsDiffer(Location from, Location to, boolean ignoreWorld) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ() || !(ignoreWorld || from.getWorld().equals(to.getWorld()));
    }

    private double distanceSquared(Location from, Location to) {
        return NumberConversions.square(from.getX() - to.getX()) + NumberConversions.square(from.getY() - to.getY()) + NumberConversions.square(from.getZ() - to.getZ());
    }
}
