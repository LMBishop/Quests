package com.leonardobishop.quests.bukkit.item.requirement;

import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Parses item requirements:
 * <pre>
 * item-requirement: STONE              # shorthand for {type: defined, material: STONE}
 * item-requirement:                    # one definition
 *   type: defined                      # checks only the given options (default)
 *   material: DIRT
 *   name: "Sword"
 * item-requirement:                    # several definitions, any of them may match
 *   - type: defined
 *     material: STONE
 *   - type: defined-exact              # builds the item and compares the full item data
 *     material: ENDER_CHEST
 * </pre>
 */
@NullMarked
public final class ItemRequirementParser {

    public static final String MATERIAL_KEY = "material";

    private ItemRequirementParser() {
    }

    /**
     * @param registry supplier of the registry holding the options available on this server
     */
    public static TaskKeyParser<ItemRequirement> parser(final Supplier<ItemOptionRegistry> registry) {
        Objects.requireNonNull(registry, "registry cannot be null");

        return (raw, context) -> parse(normalize(raw), registry.get(), context);
    }

    private static @Nullable ItemRequirement parse(final Object raw, final ItemOptionRegistry registry, final TaskKeyContext context) {
        if (raw instanceof final List<?> list) {
            if (list.isEmpty()) {
                context.error("Expected at least one item definition in '" + context.getPath() + "'");
                return null;
            }

            final List<ItemRequirement> requirements = new ArrayList<>(list.size());
            boolean failed = false;
            for (int i = 0; i < list.size(); i++) {
                final Object element = list.get(i);
                if (element instanceof List) {
                    context.error("Item definitions in '" + context.getPath() + "' cannot be nested lists");
                    return null;
                }

                final ItemRequirement requirement = element != null ? parseSingle(element, registry, context.child(String.valueOf(i))) : null;
                if (requirement == null) {
                    failed = true;
                } else {
                    requirements.add(requirement);
                }
            }

            return failed ? null : ItemRequirement.anyOf(requirements);
        }

        return parseSingle(raw, registry, context);
    }

    private static @Nullable ItemRequirement parseSingle(final Object raw, final ItemOptionRegistry registry, final TaskKeyContext context) {
        final Map<String, Object> definition;
        if (raw instanceof final Map<?, ?> map) {
            definition = new LinkedHashMap<>();
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                definition.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        } else if (raw instanceof String) {
            // shorthand: material name
            definition = new LinkedHashMap<>();
            definition.put(MATERIAL_KEY, raw);
        } else {
            context.error("Expected a material or an item definition for '" + context.getPath() + "', but got '" + raw + "' instead");
            return null;
        }

        final Object rawType = definition.getOrDefault(ItemOptionRegistry.TYPE_KEY, "defined");
        final Kind kind = Kind.fromConfigName(String.valueOf(rawType));
        if (kind == null) {
            context.child(ItemOptionRegistry.TYPE_KEY).error("Unknown item definition type '" + rawType + "' (accepted: defined, defined-exact)");
            return null;
        }

        final Map<ItemOption<?>, Object> values = new LinkedHashMap<>();
        boolean failed = false;

        for (final Map.Entry<String, Object> entry : definition.entrySet()) {
            final String key = entry.getKey();
            if (key.equals(ItemOptionRegistry.TYPE_KEY)) {
                continue;
            }

            final TaskKeyContext optionContext = context.child(key);
            final ItemOption<?> option = registry.get(key);
            if (option == null) {
                optionContext.warning("Unknown item option '" + key + "' will be ignored (options available on this server: " + registry.getKeysAsString() + ")");
                continue;
            }

            if (entry.getValue() == null) {
                continue;
            }

            final Object value = option.getParser().parse(entry.getValue(), optionContext);
            if (value == null) {
                failed = true;
                continue;
            }

            values.put(option, value);
        }

        if (failed) {
            return null;
        }

        return switch (kind) {
            case DEFINED -> new DefinedItemRequirement(values);
            case DEFINED_EXACT -> buildExact(values, context);
        };
    }

    @SuppressWarnings("unchecked")
    private static @Nullable ItemRequirement buildExact(final Map<ItemOption<?>, Object> values, final TaskKeyContext context) {
        ItemOption<Object> materialOption = null;
        for (final ItemOption<?> option : values.keySet()) {
            if (option.getKey().equals(MATERIAL_KEY)) {
                materialOption = (ItemOption<Object>) option;
            }
        }

        if (materialOption == null) {
            context.error("Item type 'defined-exact' in '" + context.getPath() + "' requires a '" + MATERIAL_KEY + "'");
            return null;
        }

        boolean exact = true;
        for (final Map.Entry<ItemOption<?>, Object> entry : values.entrySet()) {
            final ItemOption<Object> option = (ItemOption<Object>) entry.getKey();
            exact &= option.checkExact(entry.getValue(), context.child(option.getKey()));
        }
        if (!exact) {
            return null;
        }

        // the material decides the meta type, so it is applied before the meta is obtained
        final ItemStack template = new ItemStack(Material.STONE);
        materialOption.apply(template, Objects.requireNonNull(template.getItemMeta()), values.get(materialOption));

        final ItemMeta meta = template.getItemMeta();
        if (meta != null) {
            for (final Map.Entry<ItemOption<?>, Object> entry : values.entrySet()) {
                if (entry.getKey() != materialOption) {
                    ((ItemOption<Object>) entry.getKey()).apply(template, meta, entry.getValue());
                }
            }
            template.setItemMeta(meta);
        }

        return new ExactItemRequirement(template);
    }

    /**
     * Converts configuration sections (also nested ones) to plain maps, so parsers only deal with maps and lists.
     */
    private static Object normalize(final Object raw) {
        if (raw instanceof final ConfigurationSection section) {
            final Map<String, Object> map = new LinkedHashMap<>();
            for (final Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                map.put(entry.getKey(), entry.getValue() != null ? normalize(entry.getValue()) : null);
            }
            return map;
        }

        if (raw instanceof final Map<?, ?> rawMap) {
            final Map<String, Object> map = new LinkedHashMap<>();
            for (final Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put(String.valueOf(entry.getKey()), entry.getValue() != null ? normalize(entry.getValue()) : null);
            }
            return map;
        }

        if (raw instanceof final List<?> rawList) {
            final List<Object> list = new ArrayList<>(rawList.size());
            for (final Object element : rawList) {
                list.add(element != null ? normalize(element) : null);
            }
            return list;
        }

        return raw;
    }

    private enum Kind {
        DEFINED,
        DEFINED_EXACT;

        static @Nullable Kind fromConfigName(final String name) {
            return switch (name) {
                case "defined" -> DEFINED;
                case "defined-exact" -> DEFINED_EXACT;
                default -> null;
            };
        }
    }

    /**
     * {@code type: defined}: every configured option must match.
     */
    static final class DefinedItemRequirement implements ItemRequirement {

        private final Map<ItemOption<?>, Object> values;

        DefinedItemRequirement(final Map<ItemOption<?>, Object> values) {
            this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final @Nullable ItemStack item) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }

            final ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;
            for (final Map.Entry<ItemOption<?>, Object> entry : this.values.entrySet()) {
                if (!((ItemOption<Object>) entry.getKey()).matches(item, meta, entry.getValue())) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString() {
            return "defined " + this.values;
        }
    }

    /**
     * {@code type: defined-exact}: the item must be similar (all item data except the amount) to the built item.
     */
    static final class ExactItemRequirement implements ItemRequirement {

        private final ItemStack template;

        ExactItemRequirement(final ItemStack template) {
            this.template = template.clone();
        }

        @Override
        public boolean matches(final @Nullable ItemStack item) {
            return item != null && this.template.isSimilar(item);
        }

        @Override
        public String toString() {
            return "defined-exact " + this.template;
        }
    }
}
