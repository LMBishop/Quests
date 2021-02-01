package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.QuestsConfigLoader;
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

    public static void configValidateInt(String path, Object object, List<QuestsConfigLoader.ConfigProblem> problems, boolean allowNull, boolean greaterThanZero, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                        String.format("Expected an integer for '%s', but got null instead", (Object[]) args), path));
            }
            return;
        }

        try {
            Integer i = (Integer) object;
            if (greaterThanZero && i <= 0) {
                problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                        String.format("Value for field '%s' must be greater than 0", (Object[]) args), path));
            }
        } catch (ClassCastException ex) {
            problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                    String.format("Expected an integer for '%s', but got '" + object + "' instead", (Object[]) args), path));
        }
    }

    public static void configValidateBoolean(String path, Object object, List<QuestsConfigLoader.ConfigProblem> problems, boolean allowNull, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                        String.format("Expected a boolean for '%s', but got null instead", (Object[]) args), path));
            }
            return;
        }

        try {
            Boolean b = (Boolean) object;
        } catch (ClassCastException ex) {
            problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                    String.format("Expected a boolean for '%s', but got '" + object + "' instead", (Object[]) args), path));
        }
    }

    public static boolean configValidateExists(String path, Object object, List<QuestsConfigLoader.ConfigProblem> problems, String... args) {
        if (object == null) {
            problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.ERROR,
                    String.format(QuestsConfigLoader.ConfigProblemDescriptions.TASK_MISSING_FIELD.getDescription(args), (Object[]) args), path));
            return false;
        }
        return true;
    }
}
