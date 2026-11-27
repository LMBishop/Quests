package com.leonardobishop.quests.common.matcher;

import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Matches a keyed collection of an item, such as its enchantments (key: enchantment, value: level) or its
 * item flags (key: flag, value: whether it is set).
 * <p>
 * A matcher is one <b>set</b> of <b>conditions</b>, each about one key, combined by a {@link Mode}.
 * An option value is a list of sets; the option matches if <b>any</b> set matches.
 * <p>
 * Configuration shapes (see {@link #listParser(TaskKeyParser, Mode, Set)}):
 * <pre>
 * key: A                      # one set with one condition, default mode
 * key: [A, B]                 # one set, default mode
 * key:                        # one set with a mode
 *   mode: exact
 *   values: [A, B]
 * key:                        # several sets: any of them may match
 *   - [A, B]
 *   - mode: contains-any
 *     values: [C, D]
 * key: []                     # the item has none
 * </pre>
 * A list mixing conditions and sets is an error, so is the same key twice in one set.
 *
 * @param <K> key type
 * @param <V> type of the actual value of a key on the item
 */
@NullMarked
public final class CollectionMatcher<K, V> {

    private final Mode mode;
    private final List<Condition<K, V>> conditions;

    private CollectionMatcher(final Mode mode, final List<Condition<K, V>> conditions) {
        this.mode = mode;
        this.conditions = List.copyOf(conditions);
    }

    public static <K, V> CollectionMatcher<K, V> of(final Mode mode, final List<? extends Condition<K, V>> conditions) {
        Objects.requireNonNull(mode, "mode cannot be null");
        Objects.requireNonNull(conditions, "conditions cannot be null");

        return new CollectionMatcher<>(mode, List.copyOf(conditions));
    }

    @Contract(pure = true)
    public Mode getMode() {
        return this.mode;
    }

    @Contract(pure = true)
    public @Unmodifiable List<Condition<K, V>> getConditions() {
        return this.conditions;
    }

    /**
     * @param actual      the actual value of a key on the item (for keys the item does not have, the
     *                    "absent" value, e.g. level 0 or false)
     * @param presentKeys keys the item has
     */
    @Contract(pure = true)
    public boolean matches(final Function<K, V> actual, final Collection<K> presentKeys) {
        if (this.conditions.isEmpty()) {
            // an empty set always means "the item has none"
            return presentKeys.isEmpty();
        }

        return switch (this.mode) {
            case CONTAINS_ALL -> this.all(actual);
            case CONTAINS_ANY -> this.any(actual);
            case EXACT -> this.all(actual) && this.keys().containsAll(presentKeys);
            case NONE -> !this.any(actual);
        };
    }

    private boolean all(final Function<K, V> actual) {
        for (final Condition<K, V> condition : this.conditions) {
            if (!condition.test(actual.apply(condition.key()))) {
                return false;
            }
        }
        return true;
    }

    private boolean any(final Function<K, V> actual) {
        for (final Condition<K, V> condition : this.conditions) {
            if (condition.test(actual.apply(condition.key()))) {
                return true;
            }
        }
        return false;
    }

    private Set<K> keys() {
        final Set<K> keys = new HashSet<>();
        for (final Condition<K, V> condition : this.conditions) {
            keys.add(condition.key());
        }
        return keys;
    }

    @Override
    public String toString() {
        return this.mode.getConfigName() + " " + this.conditions;
    }

    /**
     * One condition of a set, about the value of one key on the item.
     */
    public interface Condition<K, V> {

        K key();

        /**
         * @return human readable name of the key, used in configuration problems
         */
        default String describeKey() {
            return String.valueOf(this.key());
        }

        /**
         * @param actual the actual value of {@link #key()} on the item ("absent" value if the item does not have it)
         */
        boolean test(V actual);
    }

    public enum Mode {
        /** Every condition holds; the item may have other keys. */
        CONTAINS_ALL,
        /** At least one condition holds. */
        CONTAINS_ANY,
        /** Every condition holds, and the item has no keys other than those of the conditions. */
        EXACT,
        /** No condition holds. */
        NONE;

        public String getConfigName() {
            return this.name().toLowerCase(Locale.ROOT).replace('_', '-');
        }

        public static @Nullable Mode fromConfigName(final String name, final Set<Mode> allowed) {
            for (final Mode mode : allowed) {
                if (mode.getConfigName().equals(name)) {
                    return mode;
                }
            }
            return null;
        }
    }

    /**
     * @param conditionParser parser of a single condition (a scalar such as {@code SHARPNESS:3+})
     * @param defaultMode     mode of sets which do not specify one
     * @param allowedModes    modes which may be configured
     * @return parser of a list of alternative sets
     */
    public static <K, V> TaskKeyParser<List<CollectionMatcher<K, V>>> listParser(final TaskKeyParser<? extends Condition<K, V>> conditionParser,
                                                                                 final Mode defaultMode, final Set<Mode> allowedModes) {
        Objects.requireNonNull(conditionParser, "conditionParser cannot be null");
        final Set<Mode> allowed = Collections.unmodifiableSet(EnumSet.copyOf(allowedModes));
        if (!allowed.contains(defaultMode)) {
            throw new IllegalArgumentException("default mode must be allowed");
        }

        return (raw, context) -> {
            if (isSetSection(raw)) {
                final CollectionMatcher<K, V> set = parseSection((Map<?, ?>) raw, conditionParser, defaultMode, allowed, context);
                return set != null ? List.of(set) : null;
            }

            if (!(raw instanceof final List<?> list)) {
                final CollectionMatcher<K, V> set = parseConditions(List.of(raw), conditionParser, defaultMode, context);
                return set != null ? List.of(set) : null;
            }

            boolean sets = false;
            boolean conditions = false;
            for (final Object element : list) {
                if (element instanceof List || isSetSection(element)) {
                    sets = true;
                } else {
                    conditions = true;
                }
            }

            if (sets && conditions) {
                context.error("'" + context.getPath() + "' mixes values and sets; use either a list of values (one set) or a list of sets");
                return null;
            }

            if (!sets) {
                final CollectionMatcher<K, V> set = parseConditions(list, conditionParser, defaultMode, context);
                return set != null ? List.of(set) : null;
            }

            final List<CollectionMatcher<K, V>> result = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                final Object element = list.get(i);
                final TaskKeyContext setContext = context.child(String.valueOf(i));
                final CollectionMatcher<K, V> set = element instanceof final List<?> l
                        ? parseConditions(l, conditionParser, defaultMode, setContext)
                        : parseSection((Map<?, ?>) element, conditionParser, defaultMode, allowed, setContext);
                if (set == null) {
                    return null;
                }
                result.add(set);
            }

            return Collections.unmodifiableList(result);
        };
    }

    private static boolean isSetSection(final @Nullable Object raw) {
        return raw instanceof final Map<?, ?> map && map.containsKey("values");
    }

    private static <K, V> @Nullable CollectionMatcher<K, V> parseSection(final Map<?, ?> map, final TaskKeyParser<? extends Condition<K, V>> conditionParser,
                                                                        final Mode defaultMode, final Set<Mode> allowed, final TaskKeyContext context) {
        for (final Object key : map.keySet()) {
            if (!"mode".equals(key) && !"values".equals(key)) {
                context.warning("Unknown option '" + key + "' in '" + context.getPath() + "' (known: mode, values)");
            }
        }

        Mode mode = defaultMode;
        final Object rawMode = map.get("mode");
        if (rawMode != null) {
            mode = Mode.fromConfigName(String.valueOf(rawMode), allowed);
            if (mode == null) {
                context.error("Unknown mode '" + rawMode + "' in '" + context.getPath() + "' (accepted: "
                        + allowed.stream().map(Mode::getConfigName).collect(Collectors.joining(", ")) + ")");
                return null;
            }
        }

        final Object rawValues = map.get("values");
        final List<?> values = rawValues instanceof final List<?> l ? l : List.of(Objects.requireNonNull(rawValues));
        return parseConditions(values, conditionParser, mode, context);
    }

    private static <K, V> @Nullable CollectionMatcher<K, V> parseConditions(final List<?> raw, final TaskKeyParser<? extends Condition<K, V>> conditionParser,
                                                                           final Mode mode, final TaskKeyContext context) {
        final Map<K, Condition<K, V>> conditions = new LinkedHashMap<>();
        boolean failed = false;

        for (final Object element : raw) {
            if (element == null) {
                context.error("'" + context.getPath() + "' contains an empty value");
                failed = true;
                continue;
            }

            final Condition<K, V> condition = conditionParser.parse(element, context);
            if (condition == null) {
                failed = true;
                continue;
            }

            if (conditions.putIfAbsent(condition.key(), condition) != null) {
                context.error("'" + context.getPath() + "' contains '" + condition.describeKey() + "' more than once in one set; use a single value (e.g. a level range) instead");
                failed = true;
            }
        }

        return failed ? null : of(mode, new ArrayList<>(conditions.values()));
    }
}
