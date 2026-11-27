package com.leonardobishop.quests.common.tasktype.key;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable, already parsed options of a single task: a {@code Map<TaskKey<T>, T>}.
 * <p>
 * Only keys registered in the {@link TaskSchema} of the task can be queried; asking for any other
 * key is a programming error and throws {@link IllegalArgumentException}.
 */
@NullMarked
public final class TaskOptions {

    private final Set<TaskKey<?>> registeredKeys;
    private final Map<TaskKey<?>, Object> values;

    public TaskOptions(final Set<TaskKey<?>> registeredKeys, final Map<TaskKey<?>, Object> values) {
        Objects.requireNonNull(registeredKeys, "registeredKeys cannot be null");
        Objects.requireNonNull(values, "values cannot be null");

        this.registeredKeys = Set.copyOf(registeredKeys);
        // keep insertion order (used e.g. by /q a info) while preventing modification
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    /**
     * @param key a registered key
     * @return whether the option was explicitly configured (and parsed successfully)
     */
    @Contract(pure = true)
    public boolean has(final TaskKey<?> key) {
        this.checkRegistered(key);

        return this.values.containsKey(key);
    }

    /**
     * @param key a registered key
     * @return the configured value, the key's default value, or null
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getOrNull(final TaskKey<T> key) {
        this.checkRegistered(key);

        final Object value = this.values.get(key);
        return value != null ? (T) value : key.getDefaultValue();
    }

    /**
     * @param key a registered key which is either required or has a default value
     * @return the configured value or the key's default value
     * @throws IllegalStateException if there is neither
     */
    public <T> T get(final TaskKey<T> key) {
        final T value = this.getOrNull(key);

        if (value == null) {
            throw new IllegalStateException(key + " has no value and no default value");
        }

        return value;
    }

    /**
     * @param key a registered key
     * @param def value returned if the option was not configured and the key has no default value
     * @return the configured value, the key's default value, or {@code def}
     */
    @Contract(pure = true)
    public <T> T getOrDefault(final TaskKey<T> key, final T def) {
        final T value = this.getOrNull(key);
        return value != null ? value : def;
    }

    /**
     * @return unmodifiable map of all configured option values
     */
    @Contract(pure = true)
    public @Unmodifiable Map<TaskKey<?>, Object> asMap() {
        return this.values;
    }

    private void checkRegistered(final TaskKey<?> key) {
        Objects.requireNonNull(key, "key cannot be null");

        if (!this.registeredKeys.contains(key)) {
            throw new IllegalArgumentException(key + " is not registered in the schema of this task");
        }
    }
}
