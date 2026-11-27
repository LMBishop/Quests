package com.leonardobishop.quests.bukkit.tasktype.type.distancefrom;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.NumberConversions;

public final class DistancefromTaskType extends BukkitTaskType<DistancefromTask> {

    private final BukkitQuestsPlugin plugin;

    public DistancefromTaskType(BukkitQuestsPlugin plugin) {
        super("distancefrom", TaskUtils.TASK_ATTRIBUTION_STRING, "Distance yourself from a set of co-ordinates.", DistancefromTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
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

        for (TaskUtils.PendingTask<DistancefromTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            DistancefromTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            World world = Bukkit.getWorld(task.world);
            if (!player.getWorld().equals(world)) {
                super.debug("World " + task.world + " does not exist or isn't the player world, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int distanceSquared = task.distance * task.distance;

            double playerDistanceSquared = distanceSquarred(player, task.x, task.y, task.z);

            super.debug("Player is " + playerDistanceSquared + "m squared away", quest.getId(), task.getId(), player.getUniqueId());

            if (playerDistanceSquared > distanceSquared) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

    private static double distanceSquarred(Player player, int x, int y, int z) {
        return NumberConversions.square(player.getX() - x)
                + NumberConversions.square(player.getY() - y)
                + NumberConversions.square(player.getZ() - z);
    }
}
