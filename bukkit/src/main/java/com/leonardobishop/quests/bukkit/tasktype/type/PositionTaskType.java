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
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (event.getPlayer().hasMetadata("NPC")) return;

        Player player = event.getPlayer();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            int x = (int) task.getConfigValue("x");
            int y = (int) task.getConfigValue("y");
            int z = (int) task.getConfigValue("z");
            String worldString = (String) task.getConfigValue("world");
            int padding = 0;
            if (task.getConfigValue("distance-padding") != null) {
                padding = (int) task.getConfigValue("distance-padding");
            }
            int paddingSquared = padding * padding;
            World world = Bukkit.getWorld(worldString);
            if (world == null) {
                super.debug("World " + worldString + " does not exist, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            Location location = new Location(world, x, y, z);
            if (player.getWorld().equals(world) && player.getLocation().getBlockX() == location.getBlockX() && player.getLocation().getBlockY() == location.getBlockY() && player.getLocation().getBlockZ() == location.getBlockZ()) {
                super.debug("Player is precisely at location", quest.getId(), task.getId(), player.getUniqueId());
                super.debug("Marking task as complete", quest.getId(), task.getId(), event.getPlayer().getUniqueId());
                taskProgress.setCompleted(true);
            } else if (padding != 0 && player.getWorld().equals(world)) {
                double playerDistanceSquared = player.getLocation().distanceSquared(location);

                super.debug("Player is " + playerDistanceSquared + "m squared away (padding squared = " + paddingSquared + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (playerDistanceSquared <= paddingSquared) {
                    super.debug("Player is within distance padding", quest.getId(), task.getId(), player.getUniqueId());
                    super.debug("Marking task as complete", quest.getId(), task.getId(), event.getPlayer().getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

}
