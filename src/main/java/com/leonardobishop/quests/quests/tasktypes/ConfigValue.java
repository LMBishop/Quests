package com.leonardobishop.quests.quests.tasktypes;

/**
 * This is for the quest creator and is purely cosmetic.
 */
public final class ConfigValue {

    private final String key;
    private final boolean required;
    private final String description;

    public ConfigValue(String key, boolean required, String description) {
        this.key = key;
        this.required = required;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }
}
