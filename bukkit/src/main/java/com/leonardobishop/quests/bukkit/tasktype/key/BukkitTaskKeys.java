package com.leonardobishop.quests.bukkit.tasktype.key;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

/**
 * Bukkit specific {@link TaskKeyParser parsers} and keys shared by many task types.
 */
@NullMarked
@SuppressWarnings("deprecation")
public final class BukkitTaskKeys {

    private BukkitTaskKeys() {
    }

    // -------------------------------------------------------------------------------------------
    // Shared keys
    // -------------------------------------------------------------------------------------------

    /** Legacy data value, used by blocks and simple (material name) items. */
    public static final TaskKey<Integer> DATA = TaskKey.of("data", TaskKeyParsers.integerValue(0, Short.MAX_VALUE));
    public static final TaskKey<Boolean> EXACT_MATCH = TaskKey.of("exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    /** {@code item} parsed together with the legacy {@code data} value. */
    public static final TaskKey<QuestItem> ITEM = TaskKey.of("item", questItem("data"));
    public static final TaskKey<List<BlockSpec>> BLOCKS = TaskKey.of("block", blocks(MaterialMode.BLOCK)).withAliases("blocks");
    public static final TaskKey<List<EntityType>> MOBS = TaskKey.of("mob", entityTypes()).withAliases("mobs");
    public static final TaskKey<List<CreatureSpawnEvent.SpawnReason>> SPAWN_REASONS = TaskKey.of("spawn-reason", spawnReasons()).withAliases("spawn-reasons");

    public static final TaskKey<Boolean> CHECK_PLAYERBLOCKTRACKER = TaskKey.of("check-playerblocktracker", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> CHECK_COREPROTECT = TaskKey.of("check-coreprotect", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Integer> CHECK_COREPROTECT_TIME = TaskKey.of("check-coreprotect-time", TaskKeyParsers.integerValueNonNegative()).withDefault(3600);
    public static final TaskKey<Boolean> ALLOW_NEGATIVE_PROGRESS = TaskKey.of("allow-negative-progress", TaskKeyParsers.booleanValue()).withDefault(true);

    /**
     * Creates a {@link TaskUtils.StringMatchMode} key defaulting to {@code EQUALS}.
     */
    public static TaskKey<TaskUtils.StringMatchMode> matchMode(final String path) {
        return TaskKey.of(path, TaskKeyParsers.enumValue(TaskUtils.StringMatchMode.class)).withDefault(TaskUtils.StringMatchMode.EQUALS);
    }

    /**
     * Creates a quest item key which reads the legacy data value from {@code dataPath}.
     */
    public static TaskKey<QuestItem> questItem(final String path, final String dataPath) {
        return TaskKey.of(path, questItem(dataPath));
    }

    // -------------------------------------------------------------------------------------------
    // Parsers
    // -------------------------------------------------------------------------------------------

    public enum MaterialMode {
        ANY {
            @Override
            public boolean isValid(final Material material) {
                return true;
            }
        },
        BLOCK {
            @Override
            public boolean isValid(final Material material) {
                return material.isBlock();
            }
        },
        ITEM {
            @Override
            public boolean isValid(final Material material) {
                return material.isItem();
            }
        };

        public abstract boolean isValid(Material material);
    }

    /**
     * Parses a single material name or a list of them.
     */
    public static TaskKeyParser<List<Material>> materials(final MaterialMode mode) {
        return TaskKeyParsers.list((raw, context) -> {
            final String name = String.valueOf(raw);
            final Material material = Material.getMaterial(name);

            if (material == null || !mode.isValid(material)) {
                unknownMaterial(context, name);
                return null;
            }

            return material;
        });
    }

    /**
     * Parses a single block or a list of blocks in the form {@code MATERIAL} or {@code MATERIAL:data}.
     */
    public static TaskKeyParser<List<BlockSpec>> blocks(final MaterialMode mode) {
        return TaskKeyParsers.list((raw, context) -> {
            final String entry = String.valueOf(raw);
            final String[] parts = entry.split(":", 2);
            final Material material = Material.getMaterial(parts[0]);

            if (material == null || !mode.isValid(material)) {
                unknownMaterial(context, entry);
                return null;
            }

            Integer data = null;
            if (parts.length == 2) {
                try {
                    data = Integer.parseInt(parts[1]);
                } catch (final NumberFormatException e) {
                    context.warning("Expected an integer data value in '" + entry + "' for '" + context.getPath() + "'");
                    return null;
                }
            }

            return new BlockSpec(material, data);
        });
    }

    public static TaskKeyParser<List<EntityType>> entityTypes() {
        return named(EntityType::valueOf, ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE);
    }

    public static TaskKeyParser<List<CreatureSpawnEvent.SpawnReason>> spawnReasons() {
        return named(CreatureSpawnEvent.SpawnReason::valueOf, ConfigProblemDescriptions.UNKNOWN_SPAWN_REASON);
    }

    public static TaskKeyParser<List<DyeColor>> dyeColors() {
        return named(DyeColor::valueOf, ConfigProblemDescriptions.UNKNOWN_DYE_COLOR);
    }

    public static TaskKeyParser<List<Enchantment>> enchantments() {
        return named(name -> {
            final Enchantment enchantment = Enchantment.getByName(name);
            if (enchantment == null) {
                throw new IllegalArgumentException(name);
            }
            return enchantment;
        }, ConfigProblemDescriptions.UNKNOWN_ENCHANTMENT);
    }

    /**
     * Parses an item, either as a material name (optionally with a legacy data value read from
     * {@code dataPath}) or as an item section. Invalid materials produce a warning and fall back to stone.
     */
    public static TaskKeyParser<QuestItem> questItem(final String dataPath) {
        return (raw, context) -> {
            final BukkitQuestsPlugin plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);

            if (raw instanceof final ConfigurationSection section) {
                validateItemSection(plugin, section, context);
                return plugin.getConfiguredQuestItem("", section);
            }

            final String name = String.valueOf(raw);
            Material material = Material.getMaterial(name);
            if (material == null) {
                unknownMaterial(context, name);
                material = Material.STONE;
            }

            final ItemStack itemStack;
            if (context.getSibling(dataPath) instanceof final Integer data) {
                itemStack = new ItemStack(material, 1, data.shortValue());
            } else {
                itemStack = new ItemStack(material, 1);
            }

            return new ParsedQuestItem("parsed", null, itemStack);
        };
    }

    // -------------------------------------------------------------------------------------------

    private static void validateItemSection(final BukkitQuestsPlugin plugin, final ConfigurationSection section, final TaskKeyContext context) {
        if (section.contains("quest-item")) {
            final String itemType = section.getString("quest-item");
            if (plugin.getQuestItemRegistry().getItem(itemType) == null) {
                context.report(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getDescription(itemType),
                        ConfigProblemDescriptions.UNKNOWN_QUEST_ITEM.getExtendedDescription(itemType),
                        "quest-item");
            }
            return;
        }

        final String materialLoc = section.contains("item") ? "item" : "type";
        if (!section.contains(materialLoc)) {
            context.report(ConfigProblem.ConfigProblemType.WARNING,
                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(""),
                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(""),
                    "type");
            return;
        }

        final String material = String.valueOf(section.get(materialLoc));
        if (!plugin.getItemGetter().isValidMaterial(material)) {
            context.report(ConfigProblem.ConfigProblemType.WARNING,
                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(material),
                    ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(material),
                    materialLoc);
        }
    }

    private static void unknownMaterial(final TaskKeyContext context, final String name) {
        context.report(ConfigProblem.ConfigProblemType.WARNING,
                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(name),
                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(name));
    }

    /**
     * List parser for values looked up by name; the lookup throws {@link IllegalArgumentException} for unknown names.
     */
    private static <E> TaskKeyParser<List<E>> named(final Function<String, E> lookup, final ConfigProblemDescriptions unknown) {
        return TaskKeyParsers.list((raw, context) -> {
            final String name = String.valueOf(raw);
            try {
                return lookup.apply(name);
            } catch (final IllegalArgumentException e) {
                context.report(ConfigProblem.ConfigProblemType.WARNING, unknown.getDescription(name), unknown.getExtendedDescription(name));
                return null;
            }
        });
    }
}
