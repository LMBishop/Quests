package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraint;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.bukkit.util.lang3.StringUtils;
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
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "BooleanMethodIsAlwaysInverted"})
public class TaskUtils {

    public static final String TASK_ATTRIBUTION_STRING = "<built-in>";
    private static final BukkitQuestsPlugin plugin;

    static {
        plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);
    }

    public static boolean validateWorld(final Player player, final Task task) {
        final Object worlds = task.getConfigValue("worlds");

        return switch (worlds) {
            case final List<?> allowedWorldNames -> allowedWorldNames.contains(player.getWorld().getName());
            case final String allowedWorldName -> allowedWorldName.equals(player.getWorld().getName());
            case null, default -> true;
        };
    }

    public static boolean validateBiome(final String biomeKey, final @NotNull Object biomes) {
        return switch (biomes) {
            case final List<?> allowedBiomeKeys-> allowedBiomeKeys.contains(biomeKey);
            case final String allowedBiomeKey -> allowedBiomeKey.equals(biomeKey);
            default -> true;
        };
    }

    private static String getBiomeKey(final @NotNull Player player) {
        final Biome biome = player.getWorld().getBiome(
                NumberConversions.floor(player.getX()),
                NumberConversions.floor(player.getY()),
                NumberConversions.floor(player.getZ())
        );
        return plugin.getVersionSpecificHandler().getBiomeKey(biome);
    }

    public static boolean doesConfigStringListExist(final @NotNull Task task, final @NotNull String key) {
        final Object configObject = task.getConfigValue(key);
        return configObject instanceof List || configObject instanceof String;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static @Nullable List<String> getConfigStringList(Task task, String key) {
        Object configObject = task.getConfigValue(key);
        if (configObject instanceof List list) {
            return List.copyOf(list);
        } else if (configObject instanceof String s){
            return List.of(s);
        } else {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static @Nullable List<Integer> getConfigIntegerList(Task task, String key) {
        Object configObject = task.getConfigValue(key);
        if (configObject instanceof List list) {
            return List.copyOf(list);
        } else if (configObject instanceof Integer i){
            return List.of(i);
        } else {
            return null;
        }
    }

    public static boolean getConfigBoolean(Task task, String key) {
        return getConfigBoolean(task, key, false);
    }

    public static boolean getConfigBoolean(Task task, String key, boolean def) {
        return task.getConfigValue(key) instanceof Boolean configBoolean ? configBoolean : def;
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
        if (taskProgress.getProgress() == null || !(taskProgress.getProgress() instanceof Double)) {
            progress = 0.0;
        } else {
            progress = (double) taskProgress.getProgress();
        }
        return progress;
    }

    public static int getIntegerTaskProgress(TaskProgress taskProgress) {
        int progress;
        if (taskProgress.getProgress() == null || !(taskProgress.getProgress() instanceof Integer)) {
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

    public static int incrementIntegerTaskProgress(TaskProgress taskProgress, int amount) {
        int progress = getIntegerTaskProgress(taskProgress) + amount;
        taskProgress.setProgress(progress);
        return progress;
    }

    public static int decrementIntegerTaskProgress(TaskProgress taskProgress) {
        int progress = getIntegerTaskProgress(taskProgress);
        taskProgress.setProgress(--progress);
        return progress;
    }

	public static void sendTrackAdvancement(Player player, Quest quest, Task task, PendingTask pendingTask, Number amount) {
        TaskProgress taskProgress = pendingTask.taskProgress();

        boolean useActionBar = plugin.getConfig().getBoolean("options.actionbar.progress", false)
                || (taskProgress.isCompleted() && plugin.getConfig().getBoolean("options.actionbar.complete", false));
        boolean useBossBar = plugin.getConfig().getBoolean("options.bossbar.progress", false)
                || (taskProgress.isCompleted() && plugin.getConfig().getBoolean("options.bossbar.complete", false));
        if (!useActionBar && !useBossBar) {
            return;
        }

        String title;

        titleSearch:
        {
            title = quest.getProgressPlaceholders().get(task.getId()); // custom title
            if (title != null) {
                break titleSearch;
            }

            title = quest.getProgressPlaceholders().get(task.getType()); // one title for all tasks of the same type
            if (title != null) {
                break titleSearch;
            }

            title = quest.getProgressPlaceholders().get("*"); // one title for all tasks
            if (title != null) {
                break titleSearch;
            }

            boolean useProgressAsFallback = plugin.getQuestsConfig().getBoolean("options.use-progress-as-fallback", true);
            if (!useProgressAsFallback) {
                return;
            }

            title = quest.getPlaceholders().get("progress"); // fallback title
            if (title != null) {
                break titleSearch;
            }

            return; // no valid title format found
        }

        QuestProgress questProgress = pendingTask.questProgress();
        title = QItemStack.processPlaceholders(plugin, title, questProgress, taskProgress);

        boolean usePlaceholderAPI = plugin.getQuestsConfig().getBoolean("options.progress-use-placeholderapi", false);
        if (usePlaceholderAPI) {
            title = plugin.getPlaceholderAPIProcessor().apply(player, title);
        }

        title = Chat.legacyColor(title);

        if (useActionBar) {
            sendTrackAdvancementActionBar(player, title);
        }

        if (useBossBar) {
            sendTrackAdvancementBossBar(player, quest, task, taskProgress, title, amount);
        }
    }

    private static void sendTrackAdvancementActionBar(Player player, String title) {
        plugin.getActionBarHandle().sendActionBar(player, title);
    }

    private static void sendTrackAdvancementBossBar(Player player, Quest quest, Task task, TaskProgress taskProgress, String title, Number amount) {
        Double bossBarProgress = null;

        if (!taskProgress.isCompleted()) {
            Object progress = taskProgress.getProgress();
            if (progress instanceof Number number) {
                bossBarProgress = number.doubleValue();
            }

            if (bossBarProgress != null) { // if has value
                bossBarProgress /= amount.doubleValue(); // calculate progress
            }
        }

        int bossBarTime = plugin.getConfig().getInt("options.bossbar.time", 5);

        if (bossBarProgress != null) {
            float bossBarFloatProgress = (float) Math.min(1.0d, Math.max(0.0d, bossBarProgress));
            plugin.getBossBarHandle().sendBossBar(player, quest.getId(), title, bossBarTime, bossBarFloatProgress);
        } else {
            plugin.getBossBarHandle().sendBossBar(player, quest.getId(), title, bossBarTime);
        }
    }

    public static List<PendingTask> getApplicableTasks(Player player, QPlayer qPlayer, TaskType type) {
        return getApplicableTasks(player, qPlayer, type, TaskConstraintSet.NONE);
    }

    public static List<PendingTask> getApplicableTasks(Player player, QPlayer qPlayer, TaskType type, TaskConstraintSet constraintSet) {
        List<PendingTask> tasks = new ArrayList<>();

        // Cache it as getting it requires some complex math
        String biomeKey = null;

        for (Quest quest : type.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(type.getType())) {
                    if (constraintSet.contains(TaskConstraint.WORLD)) {
                        if (!TaskUtils.validateWorld(player, task)) {
                            continue;
                        }
                    }

                    BIOME_CHECK:
                    if (constraintSet.contains(TaskConstraint.BIOME)) {
                        final Object biomes = task.getConfigValue("biomes");

                        if (biomes == null) {
                            break BIOME_CHECK;
                        }

                        if (biomeKey == null) {
                            biomeKey = getBiomeKey(player);
                        }

                        if (!TaskUtils.validateBiome(biomeKey, biomes)) {
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

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable Block block, @NotNull UUID player) {
        return matchBlock(type, pendingTask, block, player, "block", "blocks");
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable Block block, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        return matchBlock(type, pendingTask, block != null ? block.getState() : null, player, stringKey, listKey);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable BlockData data, @NotNull UUID player) {
        return matchBlock(type, pendingTask, data, player, "block", "blocks");
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable BlockData data, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        return matchBlock(type, pendingTask, data != null ? data.getMaterial() : null, () -> (byte) 0, player, stringKey, listKey);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable BlockState state, @NotNull UUID player) {
        return matchBlock(type, pendingTask, state, player, "block", "blocks");
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable BlockState state, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        return matchBlock(type, pendingTask, state != null ? state.getType() : null, () -> state.getRawData(), player, stringKey, listKey);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable Material blockMaterial, @NotNull Supplier<Byte> rawDataSupplier, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        Task task = pendingTask.task;

        List<String> checkBlocks = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey(stringKey) ? stringKey : listKey);
        if (checkBlocks == null) {
            return true;
        } else if (checkBlocks.isEmpty()) {
            return blockMaterial == null;
        }

        Object configData = task.getConfigValue("data");

        // do not set block data here as it will initialize Legacy Material Support
        Byte blockData = null;

        Material material;
        int comparableData;

        for (String materialName : checkBlocks) {
            String[] parts = materialName.split(":", 2);
            if (parts.length == 2) {
                comparableData = Integer.parseInt(parts[1]);
            } else if (configData != null) {
                comparableData = (int) configData;
            } else {
                comparableData = 0;
            }

            material = Material.getMaterial(parts[0]);

            type.debug("Checking against block " + material + ":" + comparableData, pendingTask.quest.getId(), task.getId(), player);

            if (material == blockMaterial) {
                if (parts.length == 1 && configData == null) {
                    type.debug("Block match (modern)", pendingTask.quest.getId(), task.getId(), player);
                    return true;
                }

                // delay legacy material support initialization
                if (blockData == null) {
                    blockData = rawDataSupplier.get();
                }

                if (blockData == comparableData) {
                    type.debug("Block match (legacy)", pendingTask.quest.getId(), task.getId(), player);
                    return true;
                }

                type.debug("Block mismatch (legacy)", pendingTask.quest.getId(), task.getId(), player);
            } else {
                type.debug("Block mismatch (modern)", pendingTask.quest.getId(), task.getId(), player);
            }
        }

        return false;
    }

    public static boolean matchEntity(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @NotNull Entity entity, @NotNull UUID player) {
        return matchEntity(type, pendingTask, entity, player, "mob", "mobs");
    }

    public static boolean matchEntity(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @NotNull Entity entity, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        return matchEntity(type, pendingTask, entity.getType(), player, stringKey, listKey);
    }

    public static boolean matchEntity(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @NotNull EntityType entityType, @NotNull UUID player) {
        return matchEntity(type, pendingTask, entityType, player, "mob", "mobs");
    }

    public static boolean matchEntity(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @NotNull EntityType entityType, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey) {
        Task task = pendingTask.task;

        List<String> checkMobs = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey(stringKey) ? stringKey : listKey);
        if (checkMobs == null) {
            return true;
        } else if (checkMobs.isEmpty()) {
            return false;
        }

        EntityType mob;

        for (String mobName : checkMobs) {
            mob = EntityType.valueOf(mobName);

            type.debug("Checking against mob " + mob, pendingTask.quest.getId(), task.getId(), player);

            if (mob == entityType) {
                type.debug("Mob match", pendingTask.quest.getId(), task.getId(), player);
                return true;
            } else {
                type.debug("Mob mismatch", pendingTask.quest.getId(), task.getId(), player);
            }
        }

        return false;
    }

    private static Method getEntitySpawnReasonMethod;

    static {
        try {
            TaskUtils.getEntitySpawnReasonMethod = Entity.class.getMethod("getEntitySpawnReason");
        } catch (final NoSuchMethodException ignored) {
            // server version cannot support the method (doesn't work on Spigot)
        }
    }

    public static boolean matchSpawnReason(final @NotNull BukkitTaskType type, final @NotNull PendingTask pendingTask, final @NotNull Entity entity,
                                           final @NotNull UUID player) {
        if (TaskUtils.getEntitySpawnReasonMethod == null) {
            type.debug("Spawn reason is specified but the server software doesn't have the method necessary to get it", pendingTask.quest.getId(), pendingTask.task.getId(), player);

            // it is supported only on Paper, so we simply ignore it
            return true;
        }

        final CreatureSpawnEvent.SpawnReason spawnReason;
        try {
            spawnReason = (CreatureSpawnEvent.SpawnReason) TaskUtils.getEntitySpawnReasonMethod.invoke(entity);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            // it should never happen
            return false;
        }

        return TaskUtils.matchEnum(CreatureSpawnEvent.SpawnReason.class, type, pendingTask, spawnReason, player, "spawn-reason", "spawn-reasons");
    }

    public static <E extends Enum<E>> boolean matchEnum(final @NotNull Class<E> enumClass, final @NotNull BukkitTaskType type, final @NotNull PendingTask pendingTask,
                                                        final @Nullable E enumValue, final @NotNull UUID player, final @NotNull String stringKey, final @NotNull String listKey) {
        final Task task = pendingTask.task;

        final List<String> checkValueStrings = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey(stringKey) ? stringKey : listKey);
        if (checkValueStrings == null) {
            return true;
        } else if (checkValueStrings.isEmpty()) {
            return enumValue == null;
        }

        E checkValue;

        for (final String checkValueString : checkValueStrings) {
            checkValue = Enum.valueOf(enumClass, checkValueString);

            type.debug("Checking against enum value " + checkValue, pendingTask.quest.getId(), task.getId(), player);

            if (checkValue == enumValue) {
                type.debug("Enum value match", pendingTask.quest.getId(), task.getId(), player);
                return true;
            } else {
                type.debug("Enum value mismatch", pendingTask.quest.getId(), task.getId(), player);
            }
        }

        return false;
    }

    public enum StringMatchMode {
        EQUALS {
            @Override
            public boolean matches(@NotNull String str1, @NotNull String str2, boolean ignoreCase) {
                return StringUtils.equals(str1, str2, ignoreCase);
            }
        },
        STARTS_WITH {
            @Override
            public boolean matches(@NotNull String str, @NotNull String prefix, boolean ignoreCase) {
                return StringUtils.startsWith(str, prefix, ignoreCase);
            }
        },
        ENDS_WITH {
            @Override
            public boolean matches(@NotNull String str, @NotNull String suffix, boolean ignoreCase) {
                return StringUtils.endsWith(str, suffix, ignoreCase);
            }
        };

        public abstract boolean matches(@NotNull String str1, @NotNull String str2, boolean ignoreCase);
    }

    /**
     * @param legacyColor whether {@link Chat#legacyColor(String)} method ought to be used on {@code string} before the comparison
     */
    public static boolean matchString(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @Nullable String string, @NotNull UUID player, @NotNull String stringKey, @NotNull String listKey, boolean legacyColor, @NotNull String matchModeKey, boolean ignoreCase) {
        Task task = pendingTask.task;

        List<String> checkNames = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey(stringKey) ? stringKey : listKey);
        if (checkNames == null) {
            return true;
        } else if (checkNames.isEmpty()) {
            return string == null;
        }

        if (string == null) {
            return false;
        }

        if (legacyColor) {
            string = Chat.legacyColor(string);
        }

        StringMatchMode matchMode;

        String matchModeString = (String) task.getConfigValue(matchModeKey);
        if (matchModeString != null) {
            matchMode = StringMatchMode.valueOf(matchModeString);
        } else {
            matchMode = StringMatchMode.EQUALS;
        }

        type.debug("Utilising " + matchMode + " mode for checking", pendingTask.quest.getId(), task.getId(), player);

        for (String name : checkNames) {
            type.debug("Checking against name " + string, pendingTask.quest.getId(), task.getId(), player);

            if (matchMode.matches(string, name, ignoreCase)) {
                type.debug("Name match", pendingTask.quest.getId(), task.getId(), player);
                return true;
            } else {
                type.debug("Name mismatch", pendingTask.quest.getId(), task.getId(), player);
            }
        }

        return false;
    }

    /**
     * @param legacyColor whether {@link Chat#legacyColor(String)} method ought to be used on {@code strings} before the comparison
     */
    public static boolean matchAnyString(@NotNull BukkitTaskType type, @NotNull PendingTask pendingTask, @NotNull String @Nullable [] strings, @NotNull UUID player, final @NotNull String stringKey, final @NotNull String listKey, boolean legacyColor, @NotNull String matchModeKey, boolean ignoreCase) {
        Task task = pendingTask.task;

        List<String> checkNames = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey(stringKey) ? stringKey : listKey);
        if (checkNames == null) {
            return true;
        } else if (checkNames.isEmpty()) {
            return strings == null || strings.length == 0;
        }

        if (strings == null || strings.length == 0) {
            return false;
        }

        if (legacyColor) {
            for (int i = 0; i < strings.length; i++) {
                strings[i] = Chat.legacyColor(strings[i]);
            }
        }

        StringMatchMode matchMode;

        String matchModeString = (String) task.getConfigValue(matchModeKey);
        if (matchModeString != null) {
            matchMode = StringMatchMode.valueOf(matchModeString);
        } else {
            matchMode = StringMatchMode.EQUALS;
        }

        type.debug("Utilising " + matchMode + " mode for checking", pendingTask.quest.getId(), task.getId(), player);

        for (String name : checkNames) {
            type.debug("Checking against name " + name, pendingTask.quest.getId(), task.getId(), player);

            for (String string : strings) {
                if (matchMode.matches(string, name, ignoreCase)) {
                    type.debug("Name match", pendingTask.quest.getId(), task.getId(), player);
                    return true;
                }
            }

            type.debug("Name mismatch", pendingTask.quest.getId(), task.getId(), player);
        }

        return false;
    }

    public static int[] getAmountsPerSlot(Player player, QuestItem qi, boolean exactMatch) {
        int[] slotToAmount = new int[37];
        // idx 36 = total
        for (int i = 0; i < 36; i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null || !qi.compareItemStack(slot, exactMatch)) continue;
            slotToAmount[36] = slotToAmount[36] + slot.getAmount();
            slotToAmount[i] = slot.getAmount();
        }
        return slotToAmount;
    }

    public static void removeItemsInSlots(Player player, int[] amountPerSlot, int amountToRemove) {
        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < 36 && amountToRemove > 0; i++) {
            if (amountPerSlot[i] != 0) {
                amountToRemove -= plugin.getVersionSpecificHandler().removeItem(inventory, i, amountToRemove);
            }
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
     * paths is a long.
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useLongConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object object = config.get(path);

                if (object == null) {
                    continue;
                }

                try {
                    Long l = (Long) object;
                } catch (ClassCastException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR,
                            "Expected a long for '" + path + "', but got '" + object + "' instead", null, path));
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

    public enum MaterialListConfigValidatorMode {
        ANY {
            @Override
            public boolean isValid(Material material) {
                return true;
            }
        },
        BLOCK {
            @Override
            public boolean isValid(Material material) {
                return material.isBlock();
            }
        },
        ITEM {
            @Override
            public boolean isValid(Material material) {
                return material.isItem();
            }
        };

        public abstract boolean isValid(Material material);
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
    public static TaskType.ConfigValidator useMaterialListConfigValidator(TaskType type, MaterialListConfigValidatorMode mode, String... paths) {
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
                    final Material material = Material.getMaterial(String.valueOf(split[0]));
                    if (material == null || !mode.isValid(material)) {
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
                                ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE.getDescription(entity),
                                ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE.getExtendedDescription(entity),
                                path));
                    }
                }
                break;
            }
        };
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a valid list of spawn reasons.
     * <p>
     * The list of entities is expected to be in the format of:
     * <pre>key: "SPAWN_REASON"</pre>
     * where SPAWN_REASON is the name of an entity. Alternatively, the list
     * of entities can be in the format of:
     * <pre>key:
     *   - "SPAWN_REASON"
     *   - "..."</pre>
     * </p>
     *
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useSpawnReasonListConfigValidator(TaskType type, String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                Object configObject = config.get(path);

                List<String> checkSpawnReasons = new ArrayList<>();
                if (configObject instanceof List<?> configList) {
                    for (Object object : configList) {
                        checkSpawnReasons.add(String.valueOf(object));
                    }
                } else {
                    if (configObject == null) {
                        continue;
                    }
                    checkSpawnReasons.add(String.valueOf(configObject));
                }

                for (String spawnReason : checkSpawnReasons) {
                    try {
                        CreatureSpawnEvent.SpawnReason.valueOf(spawnReason);
                    } catch (IllegalArgumentException ex) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_SPAWN_REASON.getDescription(spawnReason),
                                ConfigProblemDescriptions.UNKNOWN_SPAWN_REASON.getExtendedDescription(spawnReason),
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
     * paths is present in the enum.
     *
     * Should be used for small enums only as it lists possible values in config
     * problem extended description.
     *
     * @param clazz the enum class
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static <T extends Enum<T>> TaskType.ConfigValidator useEnumConfigValidator(TaskType type, Class<T> clazz, String... paths) {
        List<String> acceptedValues = new ArrayList<>();
        T[] constants = clazz.getEnumConstants();
        for (T constant : constants) {
            String acceptedValue = constant.name();
            acceptedValues.add(acceptedValue);
        }
        return useAcceptedValuesConfigValidator(type, acceptedValues, paths);
    }

    /**
     * Returns a config validator which checks if at least one value in the given
     * paths is a value in the list of accepted values.
     *
     * @param acceptedValues a list of accepted values
     * @param paths a list of valid paths for task
     * @return config validator
     */
    public static TaskType.ConfigValidator useAcceptedValuesConfigValidator(TaskType type, Collection<String> acceptedValues, String... paths) {
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
