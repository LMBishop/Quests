package com.leonardobishop.quests.common.quest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Task {

    private final Map<String, Object> configValues = new HashMap<>();
    private final String id;
    private final String type;

    public Task(String id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * @return the id of this task
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * @return the configured task type for this task
     */
    public @NotNull String getType() {
        return type;
    }

    /**
     * Get a specific configuration value for this task
     *
     * @param key key of config value to get
     * @return config value, or null
     */
    public @Nullable Object getConfigValue(@NotNull String key) {
        Objects.requireNonNull(key, "key cannot be null");

        return configValues.getOrDefault(key, null); //??? this will return null without the need of `OrDefault(key, null)`
    }

    /**
     * Get a specific configuration value for this task
     *
     * @param key key of config value to get
     * @param def default value if null
     * @return config value, or null
     */
    public @Nullable Object getConfigValue(@NotNull String key, @Nullable Object def) {
        Objects.requireNonNull(key, "key cannot be null");

        return configValues.getOrDefault(key, def);
    }

    /**
     * @return immutable list containing all config values
     */
    public @NotNull Map<String, Object> getConfigValues() {
        return Collections.unmodifiableMap(configValues);
    }

    /**
     * Add a key-value pair to this tasks configuration
     *
     * @param key key
     * @param value value
     */
    public void addConfigValue(@NotNull String key, @NotNull Object value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        configValues.put(key, value);
    }

}
