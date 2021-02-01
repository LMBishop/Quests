package com.leonardobishop.quests.quests.tasktypes;

/**
 * This is for the quest creator and is purely cosmetic.
 */
public final class ConfigValue {

    private final String key;
    private final boolean required;
    private final String description;
    private final String[] requirementExceptions;

    public ConfigValue(String key, boolean required, String description, String... requirementExceptions) {
        this.key = key;
        this.required = required;
        this.description = description;
        this.requirementExceptions = requirementExceptions;
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

    public String[] getRequirementExceptions() {
        return requirementExceptions;
    }
}
