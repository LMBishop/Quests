package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.quests.Task;
import org.bukkit.entity.Player;

import java.util.List;

public class TaskUtils {

    public static boolean validateWorld(Player player, Task task) {
        return validateWorld(player.getLocation().getWorld().getName(), task.getConfigValue("worlds"));
    }

    public static boolean validateWorld(String worldName, Task task) {
        return validateWorld(worldName, task.getConfigValue("worlds"));
    }

    public static boolean validateWorld(String worldName, Object configurationData) {
        if (configurationData == null) {
            return true;
        }

        if (configurationData instanceof List) {
            List allowedWorlds = (List) configurationData;
            if (!allowedWorlds.isEmpty() && allowedWorlds.get(0) instanceof String) {
                List<String> allowedWorldNames = (List<String>) allowedWorlds;
                return allowedWorldNames.contains(worldName);
            }
            return true;
        }

        if (configurationData instanceof String) {
            String allowedWorld = (String) configurationData;
            return worldName.equals(allowedWorld);
        }

        return true;
    }
}
