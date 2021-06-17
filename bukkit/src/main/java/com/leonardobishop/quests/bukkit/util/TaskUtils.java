package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;

import java.util.List;

public class TaskUtils {

    public static String TASK_ATTRIBUTION_STRING = "<built-in>";

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

    public static void configValidateInt(String path, Object object, List<ConfigProblem> problems, boolean allowNull, boolean greaterThanZero, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected an integer for '%s', but got null instead", (Object[]) args), path));
            }
            return;
        }

        try {
            Integer i = (Integer) object;
            if (greaterThanZero && i <= 0) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Value for field '%s' must be greater than 0", (Object[]) args), path));
            }
        } catch (ClassCastException ex) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format("Expected an integer for '%s', but got '" + object + "' instead", (Object[]) args), path));
        }
    }

    public static void configValidateBoolean(String path, Object object, List<ConfigProblem> problems, boolean allowNull, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected a boolean for '%s', but got null instead", (Object[]) args), path));
            }
            return;
        }

        try {
            Boolean b = (Boolean) object;
        } catch (ClassCastException ex) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format("Expected a boolean for '%s', but got '" + object + "' instead", (Object[]) args), path));
        }
    }

    public static boolean configValidateExists(String path, Object object, List<ConfigProblem> problems, String... args) {
        if (object == null) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format(ConfigProblemDescriptions.TASK_MISSING_FIELD.getDescription(args), (Object[]) args), path));
            return false;
        }
        return true;
    }
}
