package com.leonardobishop.quests.common.config;

import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class ConfigProblem {

    private final ConfigProblemType type;
    private final String description;
    private final @Nullable String extendedDescription;
    private final String location;

    public ConfigProblem(final ConfigProblemType type, final @Nullable String description, final @Nullable String extendedDescription, final @Nullable String location) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.description = Objects.requireNonNullElse(description, "?");
        this.extendedDescription = extendedDescription;
        this.location = Objects.requireNonNullElse(location, "?");
    }

    public ConfigProblem(final ConfigProblemType type, final @Nullable String description, final @Nullable String extendedDescription) {
        this(type, description, extendedDescription, null);
    }

    @Contract(pure = true)
    public ConfigProblemType getType() {
        return this.type;
    }

    @Contract(pure = true)
    public String getDescription() {
        return this.description;
    }

    @Contract(pure = true)
    public @Nullable String getExtendedDescription() {
        return this.extendedDescription;
    }

    @Contract(pure = true)
    public String getLocation() {
        return this.location;
    }

    public enum ConfigProblemType {
        ERROR("Error", "E", 1, "An error prevents a quest from being loaded"),
        WARNING("Warning", "W", 2, "A warning indicates a quest may not work as expected");

        private final String title;
        private final String shortened;
        private final int priority;
        private final String description;

        ConfigProblemType(final String title, final String shortened, final int priority, final String description) {
            this.title = title;
            this.shortened = shortened;
            this.priority = priority;
            this.description = description;
        }

        @Contract(pure = true)
        public String getTitle() {
            return this.title;
        }

        @Contract(pure = true)
        public String getShortened() {
            return this.shortened;
        }

        @Contract(pure = true)
        public int getPriority() {
            return this.priority;
        }

        @Contract(pure = true)
        public String getDescription() {
            return this.description;
        }
    }
}
