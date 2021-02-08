package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PositionTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public PositionTaskType() {
        super("position", "LMBishop", "Reach a set of co-ordinates.");
        this.creatorConfigValues.add(new ConfigValue("x", true, "X position."));
        this.creatorConfigValues.add(new ConfigValue("y", true, "Y position."));
        this.creatorConfigValues.add(new ConfigValue("z", true, "Z position."));
        this.creatorConfigValues.add(new ConfigValue("world", true, "Name of world."));
        this.creatorConfigValues.add(new ConfigValue("distance-padding", false, "Padding zone in meters/blocks (default/unspecified = 0)."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".world", config.get("world"), problems, "world", super.getType());
        if (TaskUtils.configValidateExists(root + ".x", config.get("x"), problems, "x", super.getType()))
            TaskUtils.configValidateInt(root + ".x", config.get("x"), problems, false, false, "x");
        if (TaskUtils.configValidateExists(root + ".y", config.get("y"), problems, "y", super.getType()))
            TaskUtils.configValidateInt(root + ".y", config.get("y"), problems, false, false, "y");
        if (TaskUtils.configValidateExists(root + ".z", config.get("z"), problems, "z", super.getType()))
            TaskUtils.configValidateInt(root + ".z", config.get("z"), problems, false, false, "z");
        TaskUtils.configValidateInt(root + ".distance-padding", config.get("distance-padding"), problems, true, true, "distance-padding");
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

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
                        continue;
                    }

                    Location location = new Location(world, x, y, z);
                    if (player.getWorld().equals(world) && player.getLocation().getBlockX() == location.getBlockX() && player.getLocation().getBlockY() == location.getBlockY() && player.getLocation().getBlockZ() == location.getBlockZ()) {
                        taskProgress.setCompleted(true);
                    } else if (padding != 0 && player.getWorld().equals(world) && player.getLocation().distanceSquared(location) < paddingSquared) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
