package com.leonardobishop.quests.common.config;

public enum ConfigProblemDescriptions {

    MALFORMED_YAML(
            "Malformed YAML file, cannot read config",
            "Your configuration is not a valid YAML configuration.<br>" +
                    "Quests cannot parse this file. Please resolve the error<br>" +
                    "below, or use an online YAML checker.<br><br>" +
                    "%s"
    ),
    INVALID_QUEST_ID(
            "ID '%s' is invalid, must be alphanumeric, unique and with no spaces",
            "ID '%s' is invalid. Either another quest is<br>" +
                    "using this ID, or it is not alphanumeric.<br><br>" +
                    "Examples:<br>" +
                    "Valid: 'abc'<br>" +
                    "Valid: 'ab-c'<br>" +
                    "Valid: 'ab-1'<br>" +
                    "Invalid: 'ab c'<br>" +
                    "Invalid: ' '"
    ),
    NO_TASKS(
            "Quest contains no valid tasks",
            "The quest does not contain any valid tasks.<br>" +
                    "This may be because of an improperly indented<br>" +
                    "tasks section, or because all specified task<br>" +
                    "types do not exist."
    ),
    NO_TASK_TYPE("Task type not specified",
            "You have not specified a type for this task.<br>" +
                    "You can specify one with the 'type:' field."
    ),
    UNKNOWN_TASK_TYPE("Task type '%s' does not exist",
            "The task type '%s' does not exist.<br>" +
                    "This may be because of a mis-typed name, or<br>" +
                    "a plugin which this task type depends on is<br>" +
                    "not present on the server."
    ),
    NO_DISPLAY_NAME("No name specified",
            "No display name for this quest has been<br>" +
                    "specified. This name is used in all chat messages<br>" +
                    "and quest GUIs."
    ),
    NO_DISPLAY_MATERIAL("No material specified",
            "No material for this quest display item<br>" +
                    "has been specified."
    ),
    UNKNOWN_QUEST_ITEM("Quest item '%s' does not exist",
            "A quest item named '%s' does not exist.<br>" +
                    "Quest items are stored in /plugins/Quests/items,<br>" +
                    "by their ID. The ID does not include the .yml<br>" +
                    "extension."
    ),
    UNKNOWN_MATERIAL("Material '%s' does not exist",
            "Material '%s' does not exist on the server.<br>" +
                    "Please refer to the wiki for a list of javadocs<br>" +
                    "corresponding to your server version. Alternatively,<br>" +
                    "you can find the material list by searching for your<br>" +
                    "server version + 'Material ENUM'."
    ),
    UNKNOWN_DYE_COLOR("Dye color '%s' does not exist",
            "Dye color '%s' does not exist on the server.<br>" +
                    "Please refer to the wiki for a list of javadocs<br>" +
                    "corresponding to your server version. Alternatively,<br>" +
                    "you can find the material list by searching for your<br>" +
                    "server version + 'DyeColor ENUM'."),
    UNKNOWN_ENCHANTMENT("Enchantment '%s' does not exist",
            "Enchantment '%s' does not exist on the server.<br>" +
                    "Please refer to the wiki for a list of javadocs<br>" +
                    "corresponding to your server version. Alternatively,<br>" +
                    "you can find the material list by searching for your<br>" +
                    "server version + 'Enchantment javadoc'."
    ),
    UNKNOWN_ENTITY_TYPE("Entity type '%s' does not exist",
            "Entity type '%s' does not exist on the server.<br>" +
                    "Please refer to the wiki for a list of javadocs<br>" +
                    "corresponding to your server version. Alternatively,<br>" +
                    "you can find the material list by searching for your<br>" +
                    "server version + 'EntityType ENUM'."),
    TASK_MALFORMED_NOT_SECTION("Task '%s' is not a configuration section",
            "Task '%s' is not properly formatted as a<br>" +
                    "configuration section. Please review the wiki<br>" +
                    "for the correct format of a task type."
    ),
    TASK_MISSING_FIELD("Required field '%s' is missing for task type '%s'",
            "Field '%s' must be set for task '%s'<br>" +
                    "to function as expected. Please review<br>" +
                    "the relevant documentation on the wiki for<br>" +
                    "a list of mandatory fields."
    ),
    UNKNOWN_TASK_REFERENCE("Attempt to reference unknown task '%s'",
            "A task by the ID '%s' has not been configured<br>" +
                    "for this quest. Note that the task ID can differ<br>" +
                    "from its task type. The ID is set by you; it is<br>" +
                    "the key for the task configuration section itself.<br><br>" +
                    "" +
                    "Example (highlighted in <bold>bold</bold>):<br>" +
                    "tasks:<br>" +
                    "<dark_grey>-></dark_grey><bold>task-id</bold>:<br>" +
                    "<dark_grey>---></dark_grey>type: ...'"
    ),
    UNKNOWN_CATEGORY("Category '%s' does not exist",
            "Category by the ID '%s' does not exist."
    ),
    UNKNOWN_REQUIREMENT("Quest requirement '%s' does not exist",
                    "This may be the result of a cascading error<br>" +
                    "if '%s' failed to load, or a mis-typed ID."
    ),
    NOT_ACCEPTED_VALUE("Value '%s' is not in the list of accepted values for task %s", null);

    private final String description;
    private final String extendedDescription;

    ConfigProblemDescriptions(String description, String extendedDescription) {
        this.description = description;
        this.extendedDescription = extendedDescription;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public String getDescription(String... format) {
        return String.format(description, (Object[]) format);
    }

    public String getExtendedDescription(String... format) {
        return String.format(extendedDescription, (Object[]) format);
    }

}