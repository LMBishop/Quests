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

public final class DistancefromTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public DistancefromTaskType(BukkitQuestsPlugin plugin) {
        super("distancefrom", TaskUtils.TASK_ATTRIBUTION_STRING, "Distance yourself from a set of co-ordinates.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "x"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "y"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "z"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "distance"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "x"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "y"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "z"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "distance"));
    }

    //    private HashMap<String, HashMap<String, Integer>> distanceSquaredCache = new HashMap<>();
//
//    @Override
//    public void onReady() {
//        distanceSquaredCache.clear();
//        for (Quest quest : super.getRegisteredQuests()) {
//            HashMap<String, Integer> squaredDistances = new HashMap<>();
//            for (Task task : quest.getTasksOfType(super.getType())) {
//                int distance = (int) task.getConfigValue("distance");
//                squaredDistances.put(task.getId(), distance);
//            }
//            distanceSquaredCache.put(quest.getId(), squaredDistances);
//        }
//    }

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

            String worldString = (String) task.getConfigValue("world");
            World world = Bukkit.getWorld(worldString);
            if (!player.getWorld().equals(world)) {
                super.debug("World " + worldString + " does not exist or isn't the player world, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int x = (int) task.getConfigValue("x");
            int y = (int) task.getConfigValue("y");
            int z = (int) task.getConfigValue("z");
            int distance = (int) task.getConfigValue("distance");
            int distanceSquared = distance * distance;

            Location location = new Location(world, x, y, z);
            double playerDistanceSquared = player.getLocation().distanceSquared(location);

            super.debug("Player is " + playerDistanceSquared + "m squared away", quest.getId(), task.getId(), player.getUniqueId());

            if (playerDistanceSquared > distanceSquared) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}
