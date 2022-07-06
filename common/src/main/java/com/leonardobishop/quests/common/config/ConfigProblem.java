package com.leonardobishop.quests.common.config;

public final class ConfigProblem {

    private final ConfigProblemType type;
    private final String description;
    private final String extendedDescription;
    private final String location;

    public ConfigProblem(ConfigProblemType type, String description, String extendedDescription, String location) {
        this.type = type;
        this.description = description == null ? "?" : description;
        this.extendedDescription = extendedDescription;
        this.location = location == null ? "?" : location;
    }

    public ConfigProblem(ConfigProblemType type, String description, String extendedDescription) {
        this(type, description, extendedDescription, null);
    }

    public ConfigProblemType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public String getLocation() {
        return location;
    }

    public enum ConfigProblemType {

        ERROR("Error", "E", 1, "An error prevents a quest from being loaded"),
        WARNING("Warning", "W", 2, "A warning indicates a quest may not work as expected");

        private final String title;
        private final String shortened;
        private final int priority;
        private final String description;

        ConfigProblemType(String title, String shortened, int priority, String description) {
            this.title = title;
            this.shortened = shortened;
            this.priority = priority;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getShortened() {
            return shortened;
        }

        public int getPriority() {
            return priority;
        }

        public String getDescription() {
            return description;
        }
    }
}