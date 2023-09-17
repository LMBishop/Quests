package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.NumberConversions;

public final class PositionTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public PositionTaskType(BukkitQuestsPlugin plugin) {
        super("position", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a set of co-ordinates.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "x"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "y"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "z"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "x"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "y"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "z"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "distance-padding"));
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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            String worldString = (String) task.getConfigValue("world");
            if (worldString != null) {
                World world = Bukkit.getWorld(worldString);
                if (world == null) {
                    super.debug("World " + worldString + " does not exist, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else if (!to.getWorld().equals(world)) {
                    super.debug("Specific world is required, but the actual world " + to.getWorld().getName() + " does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int x = (int) task.getConfigValue("x");
            int y = (int) task.getConfigValue("y");
            int z = (int) task.getConfigValue("z");

            Location location = new Location(null, x, y, z);
            double distanceSquared = distanceSquared(location, to); // use own distanceSquared method to skip world validation
            Integer padding = (Integer) task.getConfigValue("distance-padding");

            super.debug("Player is " + distanceSquared + " meters squared away (padding = " + padding + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (padding != null && (distanceSquared <= padding * padding)) {
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
