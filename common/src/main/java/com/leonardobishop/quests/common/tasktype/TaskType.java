package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a type of task that can be used within quests. A {@link Quest}
 * will be associated with this task type if it contains at least one task
 * of this type, allowing for efficient quest management without the need
 * to iterate through every single quest.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
public abstract class TaskType {

    protected final String type;
    private final @Nullable String author;
    private final @Nullable String description;
    private final List<String> aliases;
    private final List<ConfigValidator> configValidators;
    private final List<Quest> quests;

    /**
     * Constructs a new TaskType with the specified parameters.
     *
     * @param type        the name of the task type; must not contain spaces
     * @param author      the name of the person (or people) who created this task type; can be null
     * @param description a short description of the task type; can be null
     * @param aliases     an array of alternative names for this task type; must not be null
     */
    public TaskType(final String type, final @Nullable String author, final @Nullable String description, final String... aliases) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aliases, "aliases cannot be null");

        this.type = type;
        this.author = author;
        this.description = description;
        this.aliases = List.of(aliases);
        this.configValidators = new ArrayList<>();
        this.quests = new ArrayList<>();
    }

    /**
     * Constructs a new TaskType with the specified type, author, and description.
     *
     * @param type        the name of the task type; must not contain spaces
     * @param author      the name of the person (or people) who created this task type; can be null
     * @param description a short description of the task type; can be null
     */
    public TaskType(final String type, final @Nullable String author, final @Nullable String description) {
        this(type, author, description, new String[0]);
    }

    /**
     * Constructs a new TaskType with the specified type.
     *
     * @param type the name of the task type; must not contain spaces
     */
    public TaskType(final String type) {
        this(type, null, null);
    }

    /**
     * Returns the name of this task type.
     *
     * @return the task type name
     */
    @Contract(pure = true)
    public final String getType() {
        return this.type;
    }

    /**
     * Returns the author of this task type.
     *
     * @return the author's name, or null if not specified
     */
    @Contract(pure = true)
    public final @Nullable String getAuthor() {
        return this.author;
    }

    /**
     * Returns the description of this task type.
     *
     * @return the description, or null if not specified
     */
    @Contract(pure = true)
    public final @Nullable String getDescription() {
        return this.description;
    }

    /**
     * Returns an unmodifiable list of aliases for this task type.
     *
     * @return an unmodifiable list of aliases
     */
    @Contract(pure = true)
    public final @Unmodifiable List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Returns an unmodifiable view of the list of configuration validators for this task type.
     *
     * @return an unmodifiable view of the configuration validators
     */
    @Contract(pure = true)
    public @UnmodifiableView List<ConfigValidator> getConfigValidators() {
        return Collections.unmodifiableList(this.configValidators);
    }

    /**
     * Adds a configuration validator to this task type.
     *
     * @param validator the configuration validator to add; must not be null
     */
    public void addConfigValidator(final ConfigValidator validator) {
        Objects.requireNonNull(validator, "validator cannot be null");

        this.configValidators.add(validator);
    }

    /**
     * Returns an unmodifiable list of all registered quests for this task type.
     *
     * @return an unmodifiable list of registered quests
     */
    @Contract(pure = true)
    public final @UnmodifiableView List<Quest> getRegisteredQuests() {
        return Collections.unmodifiableList(this.quests);
    }

    /**
     * Registers a quest to this task type. This is typically done when
     * all quests are initially loaded.
     *
     * @param quest the quest to register; must not be null
     */
    public final void registerQuest(final Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        if (!this.quests.contains(quest)) {
            this.quests.add(quest);
        }
    }

    /**
     * Clears all registered quests from this task type.
     */
    public final void unregisterAll() {
        this.quests.clear();
    }

    /**
     * Called when all quests have been registered to this task type.
     * This method may be called multiple times if an operator uses
     * the /quests admin reload command.
     */
    public void onReady() {
        // Not implemented here
    }

    /**
     * Called when a player starts a quest that contains a task of this type.
     *
     * @param quest      the quest being started
     * @param task       the task being started
     * @param playerUUID the UUID of the player starting the quest
     */
    public void onStart(final Quest quest, final Task task, final UUID playerUUID) {
        // Not implemented here
    }

    /**
     * Called when this task type is disabled.
     */
    public void onDisable() {
        // Not implemented here
    }

    /**
     * Returns the goal of a task.
     */
    public Object getGoal(final Task task) {
        return task.getConfigValue("amount", "-");
    }

    /**
     * A functional interface for validating task configuration.
     */
    @FunctionalInterface
    public interface ConfigValidator {

        /**
         * Validates the configuration for a task.
         *
         * @param taskConfig the configuration map for the task
         * @param problems   a list to collect any configuration problems found
         */
        void validateConfig(Map<String, Object> taskConfig, List<ConfigProblem> problems);
    }
}
