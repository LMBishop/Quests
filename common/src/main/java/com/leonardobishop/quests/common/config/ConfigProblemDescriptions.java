package com.leonardobishop.quests.common.config;

public enum ConfigProblemDescriptions {

    MALFORMED_YAML("Malformed YAML file, cannot read config"),
    INVALID_QUEST_ID("ID '%s' is invalid, must be alphanumeric, unique and with no spaces"),
    NO_TASKS("Quest contains no valid tasks"),
    NO_TASK_TYPE("Task type not specified"),
    UNKNOWN_TASK_TYPE("Task type '%s' does not exist"),
    NO_DISPLAY_NAME("No name specified"),
    NO_DISPLAY_MATERIAL("No material specified"),
    UNKNOWN_MATERIAL("Material '%s' does not exist"),
    UNKNOWN_ENTITY_TYPE("Entity type '%s' does not exist"),
    TASK_MALFORMED_NOT_SECTION("Task '%s' is not a configuration section (has no fields)"),
    TASK_MISSING_FIELD("Required field '%s' is missing for task type '%s'"),
    UNKNOWN_TASK_REFERENCE("Attempt to reference unknown task '%s'"),
    UNKNOWN_CATEGORY("Category '%s' does not exist"),
    UNKNOWN_REQUIREMENT("Quest requirement '%s' does not exist");

    private final String description;

    ConfigProblemDescriptions(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public String getDescription(String... format) {
        return String.format(description, (Object[]) format);
    }

}