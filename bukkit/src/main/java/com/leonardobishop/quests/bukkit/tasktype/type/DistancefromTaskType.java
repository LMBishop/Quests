package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DistancefromTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public DistancefromTaskType(BukkitQuestsPlugin plugin) {
        super("distancefrom", TaskUtils.TASK_ATTRIBUTION_STRING, "Distance yourself from a set of co-ordinates.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".x", config.get("x"), problems, "x", super.getType()))
            TaskUtils.configValidateInt(root + ".x", config.get("x"), problems, false, false, "x");
        if (TaskUtils.configValidateExists(root + ".y", config.get("y"), problems, "y", super.getType()))
            TaskUtils.configValidateInt(root + ".y", config.get("y"), problems, false, false, "y");
        if (TaskUtils.configValidateExists(root + ".z", config.get("z"), problems, "z", super.getType()))
            TaskUtils.configValidateInt(root + ".z", config.get("z"), problems, false, false, "z");
        if (TaskUtils.configValidateExists(root + ".distance", config.get("distance"), problems, "distance", super.getType()))
            TaskUtils.configValidateInt(root + ".distance", config.get("distance"), problems, false, true, "distance");
        return problems;
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

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int x = (int) task.getConfigValue("x");
                    int y = (int) task.getConfigValue("y");
                    int z = (int) task.getConfigValue("z");
                    String worldString = (String) task.getConfigValue("world");
                    int distance = (int) task.getConfigValue("distance");
                    int distanceSquared = distance * distance;

                    World world = Bukkit.getWorld(worldString);
                    if (world == null) {
                        continue;
                    }

                    Location location = new Location(world, x, y, z);
                    if (player.getWorld().equals(world) && player.getLocation().distanceSquared(location) > distanceSquared) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
