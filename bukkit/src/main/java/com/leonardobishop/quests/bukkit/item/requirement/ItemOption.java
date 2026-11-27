package com.leonardobishop.quests.bukkit.item.requirement;

import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A single property of an {@link ItemStack} which can be required by an item definition,
 * e.g. {@code material}, {@code name} or {@code enchantments}.
 * <p>
 * Options are registered in an {@link ItemOptionRegistry} when the plugin starts; options which the
 * running server does not support are simply not registered.
 *
 * @param <V> type of the parsed option value
 */
@NullMarked
public final class ItemOption<V> {

    private static final Pattern KEY_PATTERN = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    private final String key;
    private final String description;
    private final TaskKeyParser<V> parser;
    private final Matcher<V> matcher;
    private final @Nullable Applier<V> applier;
    private final ExactnessCheck<V> exactnessCheck;

    private ItemOption(final Builder<V> builder) {
        this.key = builder.key;
        this.description = builder.description;
        this.parser = builder.parser;
        this.matcher = Objects.requireNonNull(builder.matcher, "matcher must be set");
        this.applier = builder.applier;
        this.exactnessCheck = builder.exactnessCheck;
    }

    /**
     * @param key         option name used in configuration (kebab-case)
     * @param description short description, shown e.g. in problem descriptions
     * @param parser      parser for the configured value
     */
    public static <V> Builder<V> builder(final String key, final String description, final TaskKeyParser<V> parser) {
        return new Builder<>(key, description, parser);
    }

    @Contract(pure = true)
    public String getKey() {
        return this.key;
    }

    @Contract(pure = true)
    public String getDescription() {
        return this.description;
    }

    @Contract(pure = true)
    public TaskKeyParser<V> getParser() {
        return this.parser;
    }

    /**
     * @param item  the item, never air
     * @param meta  the item meta, or null if the item has none
     * @param value the parsed option value
     */
    public boolean matches(final ItemStack item, final @Nullable ItemMeta meta, final V value) {
        return this.matcher.matches(item, meta, value);
    }

    /**
     * Checks whether the value describes exactly one item property and can therefore be used with
     * {@code defined-exact}; reports an error to the context if it cannot.
     */
    public boolean checkExact(final V value, final TaskKeyContext context) {
        if (this.applier == null) {
            context.error("Option '" + this.key + "' cannot be used with item type 'defined-exact'");
            return false;
        }

        return this.exactnessCheck.check(value, context);
    }

    /**
     * Applies an exact value (see {@link #checkExact(Object, TaskKeyContext)}) to an item which is being built.
     */
    public void apply(final ItemStack item, final ItemMeta meta, final V value) {
        if (this.applier == null) {
            throw new IllegalStateException("Option '" + this.key + "' cannot be applied");
        }

        this.applier.apply(item, meta, value);
    }

    @Override
    public String toString() {
        return this.key;
    }

    @FunctionalInterface
    public interface Matcher<V> {
        boolean matches(ItemStack item, @Nullable ItemMeta meta, V value);
    }

    @FunctionalInterface
    public interface Applier<V> {
        void apply(ItemStack item, ItemMeta meta, V value);
    }

    @FunctionalInterface
    public interface ExactnessCheck<V> {
        /**
         * @return whether the value is exact; reports an error to the context if it is not
         */
        boolean check(V value, TaskKeyContext context);
    }

    public static final class Builder<V> {

        private final String key;
        private final String description;
        private final TaskKeyParser<V> parser;
        private @Nullable Matcher<V> matcher;
        private @Nullable Applier<V> applier;
        private ExactnessCheck<V> exactnessCheck = (value, context) -> true;

        private Builder(final String key, final String description, final TaskKeyParser<V> parser) {
            Objects.requireNonNull(key, "key cannot be null");
            if (!KEY_PATTERN.matcher(key).matches()) {
                throw new IllegalArgumentException("Option key must be kebab-case: " + key);
            }

            this.key = key;
            this.description = Objects.requireNonNull(description, "description cannot be null");
            this.parser = Objects.requireNonNull(parser, "parser cannot be null");
        }

        public Builder<V> matcher(final Matcher<V> matcher) {
            this.matcher = Objects.requireNonNull(matcher, "matcher cannot be null");
            return this;
        }

        /**
         * Makes the option usable with {@code defined-exact}.
         *
         * @param exactnessCheck decides whether a value describes exactly one property value
         * @param applier        applies such a value to an item being built
         */
        public Builder<V> exact(final ExactnessCheck<V> exactnessCheck, final Applier<V> applier) {
            this.exactnessCheck = Objects.requireNonNull(exactnessCheck, "exactnessCheck cannot be null");
            this.applier = Objects.requireNonNull(applier, "applier cannot be null");
            return this;
        }

        public ItemOption<V> build() {
            return new ItemOption<>(this);
        }
    }
}
