package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class Task {

    private final String id;
    private final String type;
    private final Map<String, Object> configValues;

    public Task(final String id, final String type) {
        this.id = id;
        this.type = type;
        this.configValues = new HashMap<>();
    }

    /**
     * @return the id of the task
     */
    @Contract(pure = true)
    public String getId() {
        return this.id;
    }

    /**
     * @return the configured task type for this task
     */
    @Contract(pure = true)
    public String getType() {
        return this.type;
    }

    /**
     * Check if a config value is set for this task
     *
     * @param key key of the config value to check
     * @return whether the specified key exists
     */
    @Contract(pure = true)
    public boolean hasConfigKey(final String key) {
        Objects.requireNonNull(key, "key cannot be null");

        return this.configValues.containsKey(key);
    }

    /**
     * Get a specific configuration value for this task
     *
     * @param key key of the config value to get
     * @return config value, or null
     */
    @Contract(pure = true)
    public @Nullable Object getConfigValue(final String key) {
        Objects.requireNonNull(key, "key cannot be null");

        return this.configValues.get(key);
    }

    /**
     * Get a specific configuration value for this task
     *
     * @param key key of config value to get
     * @param def default value if null
     * @return config value, or default
     */
    @Contract(pure = true)
    public Object getConfigValue(final String key, final Object def) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(def, "def cannot be null");

        return this.configValues.getOrDefault(key, def);
    }

    /**
     * @return immutable map of the config values
     */
    @Contract(pure = true)
    public @UnmodifiableView Map<String, Object> getConfigValues() {
        return Collections.unmodifiableMap(this.configValues);
    }

    /**
     * Add a key-value pair to this tasks configuration
     *
     * @param key   key
     * @param value value
     */
    public void addConfigValue(final String key, final Object value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        this.configValues.put(key, value);
    }
}
