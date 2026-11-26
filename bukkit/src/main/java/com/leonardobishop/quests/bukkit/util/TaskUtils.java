package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraint;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.bukkit.util.lang3.StringUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "BooleanMethodIsAlwaysInverted"})
public class TaskUtils {

    public static final String TASK_ATTRIBUTION_STRING = "<built-in>";
    private static final BukkitQuestsPlugin plugin;

    static {
        plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);
    }

    private static String getBiomeKey(final @NotNull Player player) {
        final Biome biome = player.getWorld().getBiome(
                NumberConversions.floor(player.getX()),
                NumberConversions.floor(player.getY()),
                NumberConversions.floor(player.getZ())
        );
        return plugin.getVersionSpecificHandler().getBiomeKey(biome);
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

    public static void sendTrackAdvancement(Player player, Quest quest, Task task, PendingTask<?> pendingTask, Number amount) {
        TaskProgress taskProgress = pendingTask.taskProgress();

        boolean useActionBar = plugin.getQuestsConfig().getBoolean("options.actionbar.progress", false)
                || (taskProgress.isCompleted() && plugin.getQuestsConfig().getBoolean("options.actionbar.complete", false));
        boolean useBossBar = plugin.getQuestsConfig().getBoolean("options.bossbar.progress", false)
                || (taskProgress.isCompleted() && plugin.getQuestsConfig().getBoolean("options.bossbar.complete", false));
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

    public static <T extends BukkitTask> List<PendingTask<T>> getApplicableTasks(Player player, QPlayer qPlayer, BukkitTaskType<T> type) {
        return getApplicableTasks(player, qPlayer, type, TaskConstraintSet.NONE);
    }

    public static <T extends BukkitTask> List<PendingTask<T>> getApplicableTasks(Player player, QPlayer qPlayer, BukkitTaskType<T> type, EnumSet<TaskConstraint> constraintSet) {
        List<PendingTask<T>> tasks = new ArrayList<>();

        // Cache it as getting it requires some complex math
        String biomeKey = null;

        for (Quest quest : type.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (T task : type.getTasks(quest)) {
                    if (constraintSet.contains(TaskConstraint.WORLD)) {
                        if (!task.isWorldAllowed(player.getWorld().getName())) {
                            continue;
                        }
                    }

                    if (constraintSet.contains(TaskConstraint.BIOME)) {
                        final Set<String> biomes = task.getBiomes();

                        if (biomes != null) {
                            if (biomeKey == null) {
                                biomeKey = getBiomeKey(player);
                            }

                            if (!biomes.contains(biomeKey)) {
                                continue;
                            }
                        }
                    }

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    tasks.add(new PendingTask<>(quest, task, questProgress, taskProgress));
                }
            }
        }

        return tasks;
    }

    public record PendingTask<T extends Task>(Quest quest, T task, QuestProgress questProgress, TaskProgress taskProgress) { }

    // -------------------------------------------------------------------------------------------
    // Matching against parsed task options
    // -------------------------------------------------------------------------------------------

    /**
     * @param blocks configured blocks, or null if the task does not restrict blocks
     * @param data   configured {@code data} value, or null
     */
    public static boolean matchBlock(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<BlockSpec> blocks, @Nullable Integer data, @Nullable Block block, @NotNull UUID player) {
        return matchBlock(type, pendingTask, blocks, data, block != null ? plugin.getVersionSpecificHandler().getStateNoSnapshot(block) : null, player);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<BlockSpec> blocks, @Nullable Integer data, @Nullable BlockData blockData, @NotNull UUID player) {
        return matchBlock(type, pendingTask, blocks, data, blockData != null ? blockData.getMaterial() : null, () -> (byte) 0, player);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<BlockSpec> blocks, @Nullable Integer data, @Nullable BlockState state, @NotNull UUID player) {
        return matchBlock(type, pendingTask, blocks, data, state != null ? state.getType() : null, () -> state.getRawData(), player);
    }

    public static boolean matchBlock(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<BlockSpec> blocks, @Nullable Integer data, @Nullable Material blockMaterial, @NotNull Supplier<Byte> rawDataSupplier, @NotNull UUID player) {
        if (blocks == null) {
            return true;
        } else if (blocks.isEmpty()) {
            return blockMaterial == null;
        }

        String questId = pendingTask.quest().getId();
        String taskId = pendingTask.task().getId();

        // do not set block data here as it will initialize Legacy Material Support
        Byte blockData = null;

        for (BlockSpec spec : blocks) {
            int comparableData;
            if (spec.data() != null) {
                comparableData = spec.data();
            } else if (data != null) {
                comparableData = data;
            } else {
                comparableData = 0;
            }

            type.debug("Checking against block " + spec.material() + ":" + comparableData, questId, taskId, player);

            if (spec.material() == blockMaterial) {
                if (spec.data() == null && data == null) {
                    type.debug("Block match (modern)", questId, taskId, player);
                    return true;
                }

                // delay legacy material support initialization
                if (blockData == null) {
                    blockData = rawDataSupplier.get();
                }

                if (blockData == comparableData) {
                    type.debug("Block match (legacy)", questId, taskId, player);
                    return true;
                }

                type.debug("Block mismatch (legacy)", questId, taskId, player);
            } else {
                type.debug("Block mismatch (modern)", questId, taskId, player);
            }
        }

        return false;
    }

    /**
     * @param mobs configured entity types, or null if the task does not restrict entities
     */
    public static boolean matchEntity(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<EntityType> mobs, @NotNull Entity entity, @NotNull UUID player) {
        return matchEntity(type, pendingTask, mobs, entity.getType(), player);
    }

    public static boolean matchEntity(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<EntityType> mobs, @NotNull EntityType entityType, @NotNull UUID player) {
        if (mobs == null) {
            return true;
        } else if (mobs.isEmpty()) {
            return false;
        }

        String questId = pendingTask.quest().getId();
        String taskId = pendingTask.task().getId();

        for (EntityType mob : mobs) {
            type.debug("Checking against mob " + mob, questId, taskId, player);

            if (mob == entityType) {
                type.debug("Mob match", questId, taskId, player);
                return true;
            } else {
                type.debug("Mob mismatch", questId, taskId, player);
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

    /**
     * @param spawnReasons configured spawn reasons, or null if the task does not restrict spawn reasons
     */
    public static boolean matchSpawnReason(final @NotNull BukkitTaskType<?> type, final @NotNull PendingTask<?> pendingTask, final @Nullable List<CreatureSpawnEvent.SpawnReason> spawnReasons,
                                           final @NotNull Entity entity, final @NotNull UUID player) {
        if (spawnReasons == null) {
            return true;
        }

        if (TaskUtils.getEntitySpawnReasonMethod == null) {
            type.debug("Spawn reason is specified but the server software doesn't have the method necessary to get it", pendingTask.quest().getId(), pendingTask.task().getId(), player);

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

        return TaskUtils.matchEnum(type, pendingTask, spawnReasons, spawnReason, player);
    }

    /**
     * @param values configured values, or null if the task does not restrict the value
     */
    public static <E extends Enum<E>> boolean matchEnum(final @NotNull BukkitTaskType<?> type, final @NotNull PendingTask<?> pendingTask, final @Nullable List<E> values,
                                                        final @Nullable E enumValue, final @NotNull UUID player) {
        if (values == null) {
            return true;
        } else if (values.isEmpty()) {
            return enumValue == null;
        }

        final String questId = pendingTask.quest().getId();
        final String taskId = pendingTask.task().getId();

        for (final E checkValue : values) {
            type.debug("Checking against enum value " + checkValue, questId, taskId, player);

            if (checkValue == enumValue) {
                type.debug("Enum value match", questId, taskId, player);
                return true;
            } else {
                type.debug("Enum value mismatch", questId, taskId, player);
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
     * @param names       configured names, or null if the task does not restrict the name
     * @param legacyColor whether {@link Chat#legacyColor(String)} method ought to be used on {@code string} before the comparison
     */
    public static boolean matchString(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<String> names, @NotNull StringMatchMode matchMode, @Nullable String string, @NotNull UUID player, boolean legacyColor, boolean ignoreCase) {
        if (names == null) {
            return true;
        } else if (names.isEmpty()) {
            return string == null;
        }

        if (string == null) {
            return false;
        }

        if (legacyColor) {
            string = Chat.legacyColor(string);
        }

        String questId = pendingTask.quest().getId();
        String taskId = pendingTask.task().getId();

        type.debug("Utilising " + matchMode + " mode for checking", questId, taskId, player);

        for (String name : names) {
            type.debug("Checking against name " + string, questId, taskId, player);

            if (matchMode.matches(string, name, ignoreCase)) {
                type.debug("Name match", questId, taskId, player);
                return true;
            } else {
                type.debug("Name mismatch", questId, taskId, player);
            }
        }

        return false;
    }

    /**
     * @param names       configured names, or null if the task does not restrict the names
     * @param legacyColor whether {@link Chat#legacyColor(String)} method ought to be used on {@code strings} before the comparison
     */
    public static boolean matchAnyString(@NotNull BukkitTaskType<?> type, @NotNull PendingTask<?> pendingTask, @Nullable List<String> names, @NotNull StringMatchMode matchMode, @NotNull String @Nullable [] strings, @NotNull UUID player, boolean legacyColor, boolean ignoreCase) {
        if (names == null) {
            return true;
        } else if (names.isEmpty()) {
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

        String questId = pendingTask.quest().getId();
        String taskId = pendingTask.task().getId();

        type.debug("Utilising " + matchMode + " mode for checking", questId, taskId, player);

        for (String name : names) {
            type.debug("Checking against name " + name, questId, taskId, player);

            for (String string : strings) {
                if (matchMode.matches(string, name, ignoreCase)) {
                    type.debug("Name match", questId, taskId, player);
                    return true;
                }
            }

            type.debug("Name mismatch", questId, taskId, player);
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
}
