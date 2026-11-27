package com.leonardobishop.quests.bukkit.item.requirement;

import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.matcher.CollectionMatcher;
import com.leonardobishop.quests.common.matcher.LevelRange;
import com.leonardobishop.quests.common.matcher.LoreMatcher;
import com.leonardobishop.quests.common.matcher.TextMatcher;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Built-in {@link ItemOption item options}. Options are only registered when the running server
 * supports them.
 * <p>
 * List semantics: for single-valued properties ({@code material}, {@code name}, ...) a list means
 * "any of"; for collection properties ({@code enchantments}, {@code item-flags}) it means "all of".
 */
@NullMarked
@SuppressWarnings("deprecation")
public final class DefaultItemOptions {

    private static final String META = "org.bukkit.inventory.meta.ItemMeta";

    private DefaultItemOptions() {
    }

    public static void registerAll(final ItemOptionRegistry registry) {
        registry.register(material());
        registry.register(damage());
        registry.register(name());
        registry.register(lore());
        registry.register(enchantments());

        if (CompatUtils.classExists("org.bukkit.inventory.ItemFlag")) {
            registry.register(itemFlags());
        }

        // 1.11+
        if (CompatUtils.classWithMethodExists(META, "isUnbreakable")) {
            registry.register(unbreakable());
        }

        // 1.14+
        if (CompatUtils.classWithMethodExists(META, "hasCustomModelData")) {
            registry.register(customModelData());
        }

        // 1.20.5+
        if (CompatUtils.classWithMethodExists(META, "hasEnchantmentGlintOverride")) {
            registry.register(enchantmentGlintOverride());
        }
        if (CompatUtils.classWithMethodExists(META, "isHideTooltip")) {
            registry.register(hideTooltip());
        }

        // 1.21.4+ (same check as ItemGetter20)
        if (CompatUtils.classWithMethodExists(META, "setItemModel", NamespacedKey.class)) {
            registry.register(itemModel());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Options
    // ---------------------------------------------------------------------------------------------

    /**
     * {@code material: STONE}, an item tag {@code material: "#minecraft:logs"} (1.13+), or a list of
     * them (any of); see {@link MaterialMatcher}.
     */
    public static ItemOption<List<MaterialMatcher>> material() {
        return ItemOption.builder("material", "item material or item tag", TaskKeyParsers.list(MaterialMatcher.parser()))
                .matcher((item, meta, materials) -> {
                    final Material type = item.getType();
                    for (final MaterialMatcher matcher : materials) {
                        if (matcher.matches(type)) {
                            return true;
                        }
                    }
                    return false;
                })
                .exact((materials, context) -> {
                    if (!DefaultItemOptions.<MaterialMatcher>single("material").check(materials, context)) {
                        return false;
                    }
                    if (materials.getFirst().exact() == null) {
                        context.error("Option 'material' in '" + context.getPath() + "' must be a material, not an item tag, for item type 'defined-exact'");
                        return false;
                    }
                    return true;
                }, (item, meta, materials) -> item.setType(Objects.requireNonNull(materials.getFirst().exact())))
                .build();
    }

    /** Legacy data value / durability damage; any of. */
    public static ItemOption<List<Integer>> damage() {
        return ItemOption.builder("damage", "data value or durability damage", TaskKeyParsers.integerListNonNegative())
                .matcher((item, meta, damages) -> damages.contains((int) item.getDurability()))
                .exact(single("damage"), (item, meta, damages) -> item.setDurability(damages.getFirst().shortValue()))
                .build();
    }

    /** Display name text matchers; any of. */
    public static ItemOption<List<TextMatcher>> name() {
        return ItemOption.builder("name", "display name", TaskKeyParsers.list(TextMatcher.parser(Chat::legacyColor)))
                .matcher((item, meta, names) -> {
                    final String name = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : null;
                    for (final TextMatcher matcher : names) {
                        if (matcher.matches(name)) {
                            return true;
                        }
                    }
                    return false;
                })
                .exact((names, context) -> {
                    if (!DefaultItemOptions.<TextMatcher>single("name").check(names, context)) {
                        return false;
                    }
                    if (!names.getFirst().isExact()) {
                        TextMatcher.reportNotExact(context, "Name");
                        return false;
                    }
                    return true;
                }, (item, meta, names) -> {
                    final TextMatcher name = names.getFirst();
                    if (name.getMode() == TextMatcher.Mode.EQUALS) {
                        meta.setDisplayName(name.getText());
                    }
                })
                .build();
    }

    /** One lore or several alternative lores (any of); see {@link LoreMatcher}. */
    public static ItemOption<List<LoreMatcher>> lore() {
        return ItemOption.builder("lore", "lore", LoreMatcher.listParser(Chat::legacyColor))
                .matcher((item, meta, lores) -> {
                    final List<String> lore = meta != null && meta.hasLore() ? meta.getLore() : null;
                    for (final LoreMatcher matcher : lores) {
                        if (matcher.matches(lore)) {
                            return true;
                        }
                    }
                    return false;
                })
                .exact((lores, context) -> {
                    if (lores.size() != 1) {
                        context.error("Option 'lore' in '" + context.getPath() + "' must be a single lore for item type 'defined-exact'");
                        return false;
                    }
                    if (!lores.getFirst().isExact()) {
                        context.error("Lore in '" + context.getPath() + "' must use mode 'exact' and lines with mode 'equals' without 'ignore-case' for item type 'defined-exact'");
                        return false;
                    }
                    return true;
                }, (item, meta, lores) -> {
                    final List<String> lines = new ArrayList<>();
                    for (final TextMatcher line : lores.getFirst().getLines()) {
                        lines.add(line.getText());
                    }
                    if (!lines.isEmpty()) {
                        meta.setLore(lines);
                    }
                })
                .build();
    }

    /**
     * Enchantment conditions, see {@link CollectionMatcher}: {@code SHARPNESS} (any level), {@code SHARPNESS:3},
     * {@code SHARPNESS:3+}, {@code SHARPNESS:3-} (0 to 3, absence counts), {@code SHARPNESS:3-5} and
     * {@code SHARPNESS:0} (absent). Modes: {@code contains-all} (default), {@code contains-any}, {@code exact}.
     * Enchantments stored in enchanted books count too.
     */
    public static ItemOption<List<CollectionMatcher<Enchantment, Integer>>> enchantments() {
        return ItemOption.builder("enchantments", "enchantments", CollectionMatcher.<Enchantment, Integer>listParser(enchantmentParser(),
                        CollectionMatcher.Mode.CONTAINS_ALL, EnumSet.of(CollectionMatcher.Mode.CONTAINS_ALL, CollectionMatcher.Mode.CONTAINS_ANY, CollectionMatcher.Mode.EXACT)))
                .matcher((item, meta, sets) -> {
                    final Map<Enchantment, Integer> applied = item.getEnchantments();
                    final Map<Enchantment, Integer> stored = meta instanceof final EnchantmentStorageMeta storage ? storage.getStoredEnchants() : Map.of();
                    final Set<Enchantment> present = new HashSet<>(applied.keySet());
                    present.addAll(stored.keySet());

                    for (final CollectionMatcher<Enchantment, Integer> set : sets) {
                        if (set.matches(e -> Math.max(applied.getOrDefault(e, 0), stored.getOrDefault(e, 0)), present)) {
                            return true;
                        }
                    }
                    return false;
                })
                .exact((sets, context) -> {
                    if (!exactSet(sets, "enchantments", context)) {
                        return false;
                    }
                    for (final CollectionMatcher.Condition<Enchantment, Integer> condition : sets.getFirst().getConditions()) {
                        if (!((EnchantmentCondition) condition).levels().isSingle()) {
                            context.error("Enchantment '" + condition.describeKey() + "' in '" + context.getPath() + "' needs a single level (e.g. ':5') for item type 'defined-exact'");
                            return false;
                        }
                    }
                    return true;
                }, (item, meta, sets) -> {
                    for (final CollectionMatcher.Condition<Enchantment, Integer> condition : sets.getFirst().getConditions()) {
                        final int level = ((EnchantmentCondition) condition).levels().min();
                        if (level == 0) {
                            continue;
                        }
                        if (meta instanceof final EnchantmentStorageMeta storage) {
                            storage.addStoredEnchant(condition.key(), level, true);
                        } else {
                            meta.addEnchant(condition.key(), level, true);
                        }
                    }
                })
                .build();
    }

    /**
     * Item flag conditions, see {@link CollectionMatcher}. Modes: {@code contains-all} (default),
     * {@code contains-any}, {@code exact}, {@code none}.
     */
    public static ItemOption<List<CollectionMatcher<ItemFlag, Boolean>>> itemFlags() {
        return ItemOption.builder("item-flags", "item flags", CollectionMatcher.<ItemFlag, Boolean>listParser(itemFlagParser(),
                        CollectionMatcher.Mode.CONTAINS_ALL, EnumSet.allOf(CollectionMatcher.Mode.class)))
                .matcher((item, meta, sets) -> {
                    final Set<ItemFlag> present = meta != null ? meta.getItemFlags() : Set.of();
                    for (final CollectionMatcher<ItemFlag, Boolean> set : sets) {
                        if (set.matches(present::contains, present)) {
                            return true;
                        }
                    }
                    return false;
                })
                .exact((sets, context) -> exactSet(sets, "item-flags", context), (item, meta, sets) -> {
                    for (final CollectionMatcher.Condition<ItemFlag, Boolean> condition : sets.getFirst().getConditions()) {
                        meta.addItemFlags(condition.key());
                    }
                })
                .build();
    }

    public static ItemOption<Boolean> unbreakable() {
        return ItemOption.builder("unbreakable", "unbreakable", TaskKeyParsers.booleanValue())
                .matcher((item, meta, unbreakable) -> (meta != null && meta.isUnbreakable()) == unbreakable)
                .exact((value, context) -> true, (item, meta, unbreakable) -> meta.setUnbreakable(unbreakable))
                .build();
    }

    /** Legacy integer custom model data; any of. */
    public static ItemOption<List<Integer>> customModelData() {
        return ItemOption.builder("custom-model-data", "custom model data", TaskKeyParsers.integerList())
                .matcher((item, meta, values) -> meta != null && meta.hasCustomModelData() && values.contains(meta.getCustomModelData()))
                .exact(single("custom-model-data"), (item, meta, values) -> meta.setCustomModelData(values.getFirst()))
                .build();
    }

    public static ItemOption<Boolean> enchantmentGlintOverride() {
        return ItemOption.builder("enchantment-glint-override", "enchantment glint override", TaskKeyParsers.booleanValue())
                .matcher((item, meta, glint) -> meta != null && meta.hasEnchantmentGlintOverride() && meta.getEnchantmentGlintOverride() == glint)
                .exact((value, context) -> true, (item, meta, glint) -> meta.setEnchantmentGlintOverride(glint))
                .build();
    }

    public static ItemOption<Boolean> hideTooltip() {
        return ItemOption.builder("hide-tooltip", "hidden tooltip", TaskKeyParsers.booleanValue())
                .matcher((item, meta, hide) -> (meta != null && meta.isHideTooltip()) == hide)
                .exact((value, context) -> true, (item, meta, hide) -> meta.setHideTooltip(hide))
                .build();
    }

    /** Item model key, e.g. {@code minecraft:diamond}; any of. */
    public static ItemOption<List<NamespacedKey>> itemModel() {
        return ItemOption.builder("item-model", "item model", TaskKeyParsers.list(namespacedKeyParser()))
                .matcher((item, meta, models) -> meta != null && meta.hasItemModel() && models.contains(meta.getItemModel()))
                .exact(single("item-model"), (item, meta, models) -> meta.setItemModel(models.getFirst()))
                .build();
    }

    // ---------------------------------------------------------------------------------------------
    // Parsers and helpers
    // ---------------------------------------------------------------------------------------------

    /**
     * An enchantment condition: the level of {@code enchantment} on the item (0 if absent) must be within {@code levels}.
     */
    public record EnchantmentCondition(Enchantment enchantment, LevelRange levels) implements CollectionMatcher.Condition<Enchantment, Integer> {

        @Override
        public Enchantment key() {
            return this.enchantment;
        }

        @Override
        public boolean test(final Integer level) {
            return this.levels.contains(level);
        }

        @Override
        public String describeKey() {
            return this.enchantment.getName();
        }

        @Override
        public String toString() {
            return this.enchantment.getName() + ":" + this.levels;
        }
    }

    /**
     * An item flag condition: the flag must be set.
     */
    public record ItemFlagCondition(ItemFlag flag) implements CollectionMatcher.Condition<ItemFlag, Boolean> {

        @Override
        public ItemFlag key() {
            return this.flag;
        }

        @Override
        public boolean test(final Boolean set) {
            return set;
        }

        @Override
        public String toString() {
            return this.flag.name();
        }
    }

    /**
     * {@code defined-exact} needs one set in mode {@code contains-all} or {@code exact}.
     */
    private static boolean exactSet(final List<? extends CollectionMatcher<?, ?>> sets, final String key, final TaskKeyContext context) {
        if (sets.size() != 1) {
            context.error("Option '" + key + "' in '" + context.getPath() + "' must be a single set for item type 'defined-exact'");
            return false;
        }

        final CollectionMatcher.Mode mode = sets.getFirst().getMode();
        if (mode != CollectionMatcher.Mode.CONTAINS_ALL && mode != CollectionMatcher.Mode.EXACT) {
            context.error("Option '" + key + "' in '" + context.getPath() + "' must use mode 'contains-all' or 'exact' for item type 'defined-exact'");
            return false;
        }

        return true;
    }

    private static <V> ItemOption.ExactnessCheck<List<V>> single(final String key) {
        return (values, context) -> {
            if (values.size() != 1) {
                context.error("Option '" + key + "' in '" + context.getPath() + "' must have exactly one value for item type 'defined-exact'");
                return false;
            }
            return true;
        };
    }

    private static TaskKeyParser<NamespacedKey> namespacedKeyParser() {
        return (raw, context) -> {
            final NamespacedKey key = NamespacedKey.fromString(String.valueOf(raw).toLowerCase(Locale.ROOT));
            if (key == null) {
                context.error("Invalid namespaced key '" + raw + "' in '" + context.getPath() + "'");
                return null;
            }
            return key;
        };
    }

    private static TaskKeyParser<EnchantmentCondition> enchantmentParser() {
        return (raw, context) -> {
            final String value = String.valueOf(raw).trim();
            String name = value;
            LevelRange levels = LevelRange.PRESENT;

            final int colon = value.lastIndexOf(':');
            if (colon > 0 && colon < value.length() - 1 && Character.isDigit(value.charAt(colon + 1))) {
                final LevelRange parsed = LevelRange.parse(value.substring(colon + 1));
                if (parsed == null) {
                    context.error("Invalid enchantment level '" + value.substring(colon + 1) + "' in '" + value + "' in '" + context.getPath()
                            + "' (expected e.g. 3, 3+, 3-, 3-5 or 0)");
                    return null;
                }
                levels = parsed;
                name = value.substring(0, colon);
            }

            final Enchantment enchantment = findEnchantment(name);
            if (enchantment == null) {
                context.error("Unknown enchantment '" + name + "' in '" + context.getPath() + "'");
                return null;
            }

            if (levels.isAlwaysTrue()) {
                context.warning("Enchantment condition '" + value + "' in '" + context.getPath() + "' accepts every level and has no effect");
            }

            return new EnchantmentCondition(enchantment, levels);
        };
    }

    private static TaskKeyParser<ItemFlagCondition> itemFlagParser() {
        return (raw, context) -> {
            try {
                return new ItemFlagCondition(ItemFlag.valueOf(String.valueOf(raw).trim().toUpperCase(Locale.ROOT)));
            } catch (final IllegalArgumentException e) {
                context.error("Unknown item flag '" + raw + "' in '" + context.getPath() + "'");
                return null;
            }
        };
    }

    private static final boolean NAMESPACED_ENCHANTMENTS = CompatUtils.classWithMethodExists("org.bukkit.enchantments.Enchantment", "getByKey", NamespacedKey.class);
    private static final boolean NAMESPACED_KEY_FROM_STRING = CompatUtils.classWithMethodExists("org.bukkit.NamespacedKey", "fromString", String.class);

    private static @Nullable Enchantment findEnchantment(final String name) {
        if (name.indexOf(':') >= 0) {
            if (!NAMESPACED_ENCHANTMENTS || !NAMESPACED_KEY_FROM_STRING) {
                return null;
            }
            final NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
            return key != null ? Enchantment.getByKey(key) : null;
        }

        final Enchantment byName = Enchantment.getByName(name.toUpperCase(Locale.ROOT));
        if (byName != null || !NAMESPACED_ENCHANTMENTS) {
            return byName;
        }

        return Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase(Locale.ROOT)));
    }
}
