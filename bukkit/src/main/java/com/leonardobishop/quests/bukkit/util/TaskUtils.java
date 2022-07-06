package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TaskUtils {

    public static final String TASK_ATTRIBUTION_STRING = "<built-in>";
    private static final BukkitQuestsPlugin plugin;

    static {
        plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);
    }

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

    public static List<String> getConfigStringList(Task task, String key) {
        Object configObject = task.getConfigValue(key);

        List<String> strings = new ArrayList<>();
        if (configObject instanceof List) {
            strings.addAll((List) configObject);
        } else {
            strings.add(String.valueOf(configObject));
        }
        return strings;
    }

    public static boolean getConfigBoolean(Task task, String key) {
        return getConfigBoolean(task, key, false);
    }

    public static boolean getConfigBoolean(Task task, String key, boolean def) {
        Object configObject = task.getConfigValue(key);

        boolean bool = def;
        if (configObject != null) {
            bool = (boolean) task.getConfigValue(key, def);
        }
        return bool;
    }

    public static QuestItem getConfigQuestItem(Task task, String itemKey, String dataKey) {
        Object configBlock = task.getConfigValue(itemKey);
        Object configData = task.getConfigValue(dataKey);

        QuestItem questItem;
        if (configBlock instanceof ConfigurationSection) {
            questItem = plugin.getConfiguredQuestItem("", (ConfigurationSection) configBlock);
        } else {
            Material material = Material.getMaterial(String.valueOf(configBlock));
            ItemStack is;
            if (material == null) {
                material = Material.STONE;
            }
            if (configData != null) {
                is = new ItemStack(material, 1, ((Integer) configData).shortValue());
            } else {
                is = new ItemStack(material, 1);
            }
            questItem = new ParsedQuestItem("parsed", null, is);
        }

        return questItem;
    }

    public static double getDecimalTaskProgress(TaskProgress taskProgress) {
        double progress;
        if (taskProgress.getProgress() == null) {
            progress = 0.0;
        } else {
            progress = (double) taskProgress.getProgress();
        }
        return progress;
    }

    public static int getIntegerTaskProgress(TaskProgress taskProgress) {
        int progress;
        if (taskProgress.getProgress() == null) {
            progress = 0;
        } else {
            progress = (int) taskProgress.getProgress();
        }
        return progress;
    }

    public static int incrementIntegerTaskProgress(TaskProgress taskProgress) {
        int progress = getIntegerTaskProgress(taskProgress);
        taskProgress.setProgress(++progress);
        return progress;
    }

    public static List<PendingTask> getApplicableTasks(Player player, QPlayer qPlayer, TaskType type, TaskConstraint... constraints) {
        List<PendingTask> tasks = new ArrayList<>();
        List<TaskConstraint> taskConstraints = Arrays.asList(constraints);

        for (Quest quest : type.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(type.getType())) {
                    if (taskConstraints.contains(TaskConstraint.WORLD)) {
                        if (!TaskUtils.validateWorld(player, task)) {
                            continue;
                        }
                    }

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    tasks.add(new PendingTask(quest, task, questProgress, taskProgress));
                }
            }
        }

        return tasks;
    }

    public record PendingTask(Quest quest, Task task, QuestProgress questProgress, TaskProgress taskProgress) { }

    public enum TaskConstraint {
        WORLD
    }

    public static boolean matchBlock(BukkitTaskType type, PendingTask pendingTask, Block block, UUID player) {
        Task task = pendingTask.task;

        Material material;

        Object configData = task.getConfigValue("data");

        List<String> checkBlocks = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("block") ? "block" : "blocks");

        for (String materialName : checkBlocks) {
            // LOG:1 LOG:2 LOG should all be supported with this
            String[] split = materialName.split(":");
            int comparableData = 0;
            if (configData != null) {
                comparableData = (int) configData;
            }
            if (split.length > 1) {
                comparableData = Integer.parseInt(split[1]);
            }

            material = Material.getMaterial(String.valueOf(split[0]));
            Material blockType = block.getType();

            short blockData = block.getData();

            type.debug("Checking against block " + material, pendingTask.quest.getId(), task.getId(), player);

            if (blockType == material) {
                if (((split.length == 1 && configData == null) || ((int) blockData) == comparableData)) {
                    type.debug("Block match", pendingTask.quest.getId(), task.getId(), player);
                    return true;
                } else {
                    type.debug("Data mismatch", pendingTask.quest.getId(), task.getId(), player);
                }
            } else {
                type.debug("Type mismatch", pendingTask.quest.getId(), task.getId(), player);
            }
        }
        return false;
    }

    public static int[] getAmountsPerSlot(Player player, QuestItem qi) {
        int[] slotToAmount = new int[37];
        // idx 36 = total
        for (int i = 0; i < 36; i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null || !qi.compareItemStack(slot))
                continue;
            slotToAmount[36] = slotToAmount[36] + slot.getAmount();
            slotToAmount[i] = slot.getAmount();
        }
        return slotToAmount;
    }

    public static void removeItemsInSlots(Player player, int[] amountPerSlot, int amountToRemove) {
        for (int i = 0; i < 36; i++) {
            if (amountPerSlot[i] == 0) continue;

            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null) continue;

            int amountInStack = slot.getAmount();
            int min = Math.max(0, amountInStack - amountToRemove);
            slot.setAmount(min);
            amountToRemove = amountToRemove - amountInStack;
            if (amountToRemove <= 0) break;
        }
    }

    public static void configValidateNumber(String path, Object object, List<ConfigProblem> problems, boolean allowNull, boolean greaterThanZero, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected a number for '%s', but got null instead", (Object[]) args), null, path));
            }
            return;
        }

        try {
            double d = Double.parseDouble(String.valueOf(object));
            if (greaterThanZero && d <= 0) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Value for field '%s' must be greater than 0", (Object[]) args), null, path));
            }
        } catch (ClassCastException ex) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format("Expected a number for '%s', but got '" + object + "' instead", (Object[]) args), null, path));
        }
    }

    public static void configValidateInt(String path, Object object, List<ConfigProblem> problems, boolean allowNull, boolean greaterThanZero, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected an integer for '%s', but got null instead", (Object[]) args), null, path));
            }
            return;
        }

        try {
            Integer i = (Integer) object;
            if (greaterThanZero && i <= 0) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Value for field '%s' must be greater than 0", (Object[]) args), null, path));
            }
        } catch (ClassCastException ex) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format("Expected an integer for '%s', but got '" + object + "' instead", (Object[]) args), null, path));
        }
    }

    public static void configValidateBoolean(String path, Object object, List<ConfigProblem> problems, boolean allowNull, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected a boolean for '%s', but got null instead", (Object[]) args), null, path));
            }
            return;
        }

        try {
            Boolean b = (Boolean) object;
        } catch (ClassCastException ex) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    String.format("Expected a boolean for '%s', but got '" + object + "' instead", (Object[]) args), null, path));
        }
    }

    public static void configValidateItemStack(String path, Object object, List<ConfigProblem> problems, boolean allowNull, String... args) {
        if (object == null) {
            if (!allowNull) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                        String.format("Expected item configuration for '%s', but got null instead", (Object[]) args), null, path));
            }
            return;
        }

        if (object instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) object;

            if (section.contains("quest-item")) {
                String type = section.getString("quest-item");
                if (plugin.getQuestItemRegistry().getItem(section.getString("quest-item")) == null) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getDescription(type),
                            ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getExtendedDescription(type),
                            path + ".item.quest-item"));
                }
            } else {
                String itemloc = "item";
                if (!section.contains("item")) {
                    itemloc = "type";
                }
                if (!section.contains(itemloc)) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(""),
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(""),
                            path + ".type"));
                } else {
                    String type = String.valueOf(section.get(itemloc));
                    if (!plugin.getItemGetter().isValidMaterial(type)) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(type),
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(type),
                                path + itemloc));
                    }
                }
            }
        } else {
            if (Material.getMaterial(String.valueOf(object)) == null) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(String.valueOf(object)),
                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(String.valueOf(object)),
                        path));
            }
        }
    }

    public static boolean configValidateExists(String path, Object object, List<ConfigProblem> problems, String... args) {
        if (object == null) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    ConfigProblemDescriptions.TASK_MISSING_FIELD.getDescription(args),
                    ConfigProblemDescriptions.TASK_MISSING_FIELD.getExtendedDescription(args),
                    path));
            return false;
        }
        return true;
    }
}
