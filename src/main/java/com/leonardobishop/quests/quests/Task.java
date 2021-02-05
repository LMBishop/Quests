package com.leonardobishop.quests.quests;

import java.util.HashMap;
import java.util.Map;

public class Task {

    private final Map<String, Object> configValues = new HashMap<>();
    private final String id;
    private final String type;

    public Task(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Object getConfigValue(String key) {
        return configValues.getOrDefault(key, null); //??? this will return null without the need of `OrDefault(key, null)`
    }

    public Object getConfigValue(String key, boolean def) {
        return configValues.getOrDefault(key, def);
    }

    public Map<String, Object> getConfigValues() {
        return configValues;
    }

    public void addConfigValue(String key, Object value) {
        configValues.put(key, value);
    }

}
