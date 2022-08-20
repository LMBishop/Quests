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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;

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
        } else if (configObject != null) {
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
        if (checkBlocks.isEmpty()) {
            return true;
        }

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


            type.debug("Checking against block " + material, pendingTask.quest.getId(), task.getId(), player);

            if (block != null && block.getType() == material) {
                short blockData = block.getData();
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

    public static boolean matchDyeColor(BukkitTaskType type, PendingTask pendingTask, Colorable colorable, UUID player) {
        Task task = pendingTask.task;

        DyeColor color;

        List<String> checkColors = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("color") ? "color" : "colors");
        if (checkColors.isEmpty()) {
            return true;
        }

        for (String colorName : checkColors) {
            color = DyeColor.valueOf(colorName);

            DyeColor entityColor = colorable.getColor();
            if (entityColor == null) {
                break;
            }

            type.debug("Checking against entity " + entityColor.name(), pendingTask.quest.getId(), task.getId(), player);

            if (entityColor == color) {
                type.debug("DyeColor match", pendingTask.quest.getId(), task.getId(), player);
                return true;
            } else {
                type.debug("DyeColor mismatch", pendingTask.quest.getId(), task.getId(), player);
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


    /**
     * Returns a config validator which checks if at least one value in the given
     * paths exist.
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useRequiredConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object object = config.get(path);

                if (object != null) {
                    return;
                }
            }
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                    ConfigProblemDescriptions.TASK_MISSING_FIELD.getDescription(paths[0], type.getType()),
                    ConfigProblemDescriptions.TASK_MISSING_FIELD.getExtendedDescription(paths[0], type.getType()),
                    paths[0]));
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is an item stack.
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useItemStackConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object object = config.get(path);

                if (object == null) {
                    continue;
                }

                if (object instanceof ConfigurationSection section) {

                    if (section.contains("quest-item")) {
                        String itemType = section.getString("quest-item");
                        if (plugin.getQuestItemRegistry().getItem(itemType) == null) {
                            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                    ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getDescription(itemType),
                                    ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getExtendedDescription(itemType),
                                    path + ".quest-item"));
                        }
                    } else {
                        String materialLoc = "item";
                        if (!section.contains("item")) {
                            materialLoc = "type";
                        }

                        if (!section.contains(materialLoc)) {
                            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(""),
                                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(""),
                                    path + ".type"));
                        } else {
                            String material = String.valueOf(section.get(materialLoc));
                            if (!plugin.getItemGetter().isValidMaterial(material)) {
                                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(material),
                                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(material),
                                        path + "." + materialLoc));
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
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is an integer.
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useIntegerConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object object = config.get(path);

                if (object == null) {
                    continue;
                }

                try {
                    Integer i = (Integer) object;
                } catch (ClassCastException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                            "Expected an integer for '" + path + "', but got '" + object + "' instead", null, path));
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a boolean.
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useBooleanConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object object = config.get(path);

                if (object == null) {
                    continue;
                }

                try {
                    Boolean b = (Boolean) object;
                } catch (ClassCastException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                            "Expected a boolean for '" + path + "', but got '" + object + "' instead", null, path));
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a valid list of materials.
     * <p>
     * The list of materials is expected to be in the format of:
     * <pre>key: "MATERIAL_NAME"</pre>
     * where MATERIAL_NAME is the name of a material. Alternatively, the list
     * of materials can be in the format of:
     * <pre>key:
     *   - "MATERIAL_NAME"
     *   - "..."</pre>
     * </p>
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useMaterialListConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configBlock = config.get(path);

                List<String> checkBlocks = new ArrayList<>();
                if (configBlock instanceof List<?> configList) {
                    for (Object object : configList) {
                        checkBlocks.add(String.valueOf(object));
                    }
                } else {
                    if (configBlock == null) {
                        continue;
                    }
                    checkBlocks.add(String.valueOf(configBlock));
                }

                for (String materialName : checkBlocks) {
                    String[] split = materialName.split(":");
                    if (Material.getMaterial(String.valueOf(split[0])) == null) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(materialName),
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(materialName),
                                path));
                    }
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a valid list of dye colors.
     * <p>
     * The list of dye colors is expected to be in the format of:
     * <pre>key: "DYE_COLOR_NAME"</pre>
     * where DYE_COLOR_NAME is the name of a material. Alternatively, the list
     * of materials can be in the format of:
     * <pre>key:
     *   - "DYE_COLOR_NAME"
     *   - "..."</pre>
     * </p>
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useDyeColorConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configColor = config.get(path);

                List<String> checkColors = new ArrayList<>();
                if (configColor instanceof List<?> configList) {
                    for (Object object : configList) {
                        checkColors.add(String.valueOf(object));
                    }
                } else {
                    if (configColor == null) {
                        continue;
                    }
                    checkColors.add(String.valueOf(configColor));
                }

                for (String colorName : checkColors) {
                    try {
                        DyeColor.valueOf(colorName);
                    } catch (IllegalArgumentException ex) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_DYE_COLOR.getDescription(colorName),
                                ConfigProblemDescriptions.UNKNOWN_DYE_COLOR.getExtendedDescription(colorName),
                                path));
                    }
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a valid list of entities.
     * <p>
     * The list of entities is expected to be in the format of:
     * <pre>key: "ENTITY_TYPE"</pre>
     * where ENTITY_TYPE is the name of an entity. Alternatively, the list
     * of entities can be in the format of:
     * <pre>key:
     *   - "ENTITY_TYPE"
     *   - "..."</pre>
     * </p>
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useEntityListConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configObject = config.get(path);

                List<String> checkEntities = new ArrayList<>();
                if (configObject instanceof List<?> configList) {
                    for (Object object : configList) {
                        checkEntities.add(String.valueOf(object));
                    }
                } else {
                    if (configObject == null) {
                        continue;
                    }
                    checkEntities.add(String.valueOf(configObject));
                }

                for (String entity : checkEntities) {
                    try {
                        EntityType.valueOf(entity);
                    } catch (IllegalArgumentException ex) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(entity),
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(entity),
                                path));
                    }
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a valid list of enchantments.
     * <p>
     * The list of entities is expected to be in the format of:
     * <pre>key: "ENCHANTMENT"</pre>
     * where ENCHANTMENT is the name of an entity. Alternatively, the list
     * of entities can be in the format of:
     * <pre>key:
     *   - "ENCHANTMENT"
     *   - "..."</pre>
     * </p>
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useEnchantmentListConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configObject = config.get(path);

                List<String> checkEnchantments = new ArrayList<>();
                if (configObject instanceof List<?> configList) {
                    for (Object object : configList) {
                        checkEnchantments.add(String.valueOf(object));
                    }
                } else {
                    if (configObject == null) {
                        continue;
                    }
                    checkEnchantments.add(String.valueOf(configObject));
                }

                for (String enchantment : checkEnchantments) {
                    if (Enchantment.getByName(enchantment) == null) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_ENCHANTMENT.getDescription(enchantment),
                                ConfigProblemDescriptions.UNKNOWN_ENCHANTMENT.getExtendedDescription(enchantment),
                                path));
                    }
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a value in the list of accepted values.
     *
     * @param acceptedValues a list of accepted values
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useAcceptedValuesConfigValidator(TaskType type, List<String> acceptedValues, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configObject = config.get(path);

                if (configObject == null) {
                    continue;
                }

                if (!acceptedValues.contains(String.valueOf(configObject))) {
                    String extendedDescription =
                            "The accepted values are:";
                    for (String value : acceptedValues) {
                        extendedDescription += "<br> - " + value;
                    }
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.NOT_ACCEPTED_VALUE.getDescription(String.valueOf(configObject), type.getType()),
                        extendedDescription,
                        path));
                }

                break;
            }
        };
    }
}
