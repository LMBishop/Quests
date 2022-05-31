package com.leonardobishop.quests.common.config;

public final class ConfigProblem {

    private final ConfigProblemType type;
    private final String description;
    private final String extendedDescription;
    private final String location;

    public ConfigProblem(ConfigProblemType type, String description, String extendedDescription, String location) {
        this.type = type;
        this.description = description == null ? "?" : description;
        this.extendedDescription = extendedDescription == null ? "<dark_grey>This error has no extended description</dark_grey>" : extendedDescription;
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

        ERROR("Error", "E", 1),
        WARNING("Warning", "W", 2);

        private final String title;
        private final String shortened;
        private final int priority;

        ConfigProblemType(String title, String shortened, int priority) {
            this.title = title;
            this.shortened = shortened;
            this.priority = priority;
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

    }
}