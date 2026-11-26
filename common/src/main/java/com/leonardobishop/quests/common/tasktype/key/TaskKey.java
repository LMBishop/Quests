package com.leonardobishop.quests.common.tasktype.key;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A typed task configuration option.
 * <p>
 * A key describes where a value lives in the task configuration section ({@link #getPath()}
 * plus optional {@link #getAliases() aliases}), how the raw YAML value is turned into a value of
 * type {@code T} ({@link #getParser()}), whether it must be present and what the value is
 * when it was not configured.
 * <p>
 * Keys are compared by identity, so they are meant to be declared once as {@code static final}
 * constants and registered in a {@link TaskSchema}. Every {@code with*} method returns a new key.
 *
 * @param <T> type of the parsed value
 */
@NullMarked
public final class TaskKey<T> {

    private final String path;
    private final List<String> aliases;
    private final TaskKeyParser<T> parser;
    private final @Nullable T defaultValue;
    private final boolean required;

    private TaskKey(final String path, final List<String> aliases, final TaskKeyParser<T> parser, final @Nullable T defaultValue, final boolean required) {
        this.path = path;
        this.aliases = aliases;
        this.parser = parser;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    /**
     * Creates a new optional key without a default value.
     *
     * @param path   primary path of the value inside the task section
     * @param parser parser used to convert the raw value
     * @param <T>    type of the parsed value
     * @return a new key
     */
    public static <T> TaskKey<T> of(final String path, final TaskKeyParser<T> parser) {
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(parser, "parser cannot be null");

        return new TaskKey<>(path, List.of(), parser, null, false);
    }

    /**
     * @return a copy of this key which reports an error when no value is configured
     */
    @Contract(pure = true)
    public TaskKey<T> required() {
        return new TaskKey<>(this.path, this.aliases, this.parser, this.defaultValue, true);
    }

    /**
     * @param defaultValue value returned when the option was not configured
     * @return a copy of this key with the given default value
     */
    @Contract(pure = true)
    public TaskKey<T> withDefault(final T defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue cannot be null");

        return new TaskKey<>(this.path, this.aliases, this.parser, defaultValue, this.required);
    }

    /**
     * Alternative paths are checked in order after the primary path; the first one present wins.
     * Typical usage is a singular/plural pair such as {@code block}/{@code blocks}.
     *
     * @param aliases alternative paths
     * @return a copy of this key with the given aliases appended
     */
    @Contract(pure = true)
    public TaskKey<T> withAliases(final String... aliases) {
        Objects.requireNonNull(aliases, "aliases cannot be null");

        final List<String> newAliases = new ArrayList<>(this.aliases);
        newAliases.addAll(List.of(aliases));

        return new TaskKey<>(this.path, List.copyOf(newAliases), this.parser, this.defaultValue, this.required);
    }

    /**
     * @return primary path of the value inside the task section
     */
    @Contract(pure = true)
    public String getPath() {
        return this.path;
    }

    /**
     * @return alternative paths, checked in order after the primary path
     */
    @Contract(pure = true)
    public @Unmodifiable List<String> getAliases() {
        return this.aliases;
    }

    /**
     * @return parser converting the raw value
     */
    @Contract(pure = true)
    public TaskKeyParser<T> getParser() {
        return this.parser;
    }

    /**
     * @return value used when the option was not configured, or null
     */
    @Contract(pure = true)
    public @Nullable T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * @return whether the option must be configured
     */
    @Contract(pure = true)
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public String toString() {
        return "TaskKey{" + this.path + (this.aliases.isEmpty() ? "" : "|" + String.join("|", this.aliases)) + "}";
    }
}
