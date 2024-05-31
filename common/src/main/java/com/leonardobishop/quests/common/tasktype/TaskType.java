package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A task type which can be used within Quests. A {@link Quest}
 * will be registered to this if it contains at least 1 task
 * which is of this type. This is so you do not have to
 * iterate through every single quest.
 */
public abstract class TaskType {

    protected final String type;
    private final String author;
    private final String description;
    private final Set<String> aliases;
    private final Set<Quest> quests;
    private final Set<ConfigValidator> configValidators;

    /**
     * Constructs a TaskType.
     *
     * @param type the name of the task type, should not contain spaces
     * @param author the name of the person (or people) who wrote it
     * @param description a short, simple description of the task type
     * @param aliases the aliases of the task type, should not contain spaces
     */
    public TaskType(final @NotNull String type, final @Nullable String author, final @Nullable String description, final @NotNull String @NotNull ... aliases) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aliases, "aliases cannot be null");

        this.type = type;
        this.author = author;
        this.description = description;
        this.aliases = Set.of(aliases);
        this.quests = new HashSet<>();
        this.configValidators = new HashSet<>();
    }

    /**
     * Constructs a TaskType with the specified type, author, and description.
     *
     * @param type the name of the task type, should not contain spaces
     * @param author the name of the person (or people) who wrote it
     * @param description a short, simple description of the task type
     */
    public TaskType(final @NotNull String type, final @Nullable String author, final @Nullable String description) {
        this(type, author, description, new String[0]);
    }

    /**
     * Constructs a TaskType with the specified type.
     *
     * @param type the name of the task type, should not contain spaces
     */
    public TaskType(final @NotNull String type) {
        this(type, null, null);
    }

    /**
     * Registers a {@link Quest} to this task type. This is usually done when
     * all the quests are initially loaded.
     *
     * @param quest the {@link Quest} to register.
     */
    public final void registerQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        this.quests.add(quest);
    }

    /**
     * Clears the set which contains the registered quests.
     */
    protected final void unregisterAll() {
        this.quests.clear();
    }

    /**
     * Returns an immutable set of all registered quests.
     *
     * @return immutable {@link Set} of type {@link Quest} of all registered quests.
     */
    public final @NotNull Set<Quest> getRegisteredQuests() {
        return Collections.unmodifiableSet(this.quests);
    }

    /**
     * Returns the type of this task type.
     *
     * @return the type of this task type
     */
    public final @NotNull String getType() {
        return this.type;
    }

    /**
     * Returns the author of this task type.
     *
     * @return the author of this task type, or null if not specified
     */
    public @Nullable String getAuthor() {
        return this.author;
    }

    /**
     * Returns the description of this task type.
     *
     * @return the description of this task type, or null if not specified
     */
    public @Nullable String getDescription() {
        return this.description;
    }

    /**
     * Returns the aliases of this task type.
     *
     * @return a set of aliases of this task type
     */
    public @NotNull Set<String> getAliases() {
        return this.aliases;
    }

    /**
     * Called when Quests has finished registering all quests to the task type.
     * May be called several times if an operator uses /quests admin reload.
     */
    public void onReady() {
        // not implemented here
    }

    /**
     * Called when a player starts a quest containing a task of this type.
     *
     * @param quest the quest containing the task
     * @param task the task being started
     * @param playerUUID the UUID of the player starting the task
     */
    public void onStart(final @NotNull Quest quest, final @NotNull Task task, final @NotNull UUID playerUUID) {
        // not implemented here
    }

    /**
     * Called when a task type is disabled.
     */
    public void onDisable() {
        // not implemented here
    }

    /**
     * Adds a config validator to this task type.
     *
     * @param validator the config validator to add
     */
    public void addConfigValidator(final @NotNull ConfigValidator validator) {
        Objects.requireNonNull(validator, "validator cannot be null");

        this.configValidators.add(validator);
    }

    /**
     * Returns an immutable set of config validators.
     *
     * @return an immutable set of config validators
     */
    public @NotNull Set<ConfigValidator> getConfigValidators() {
        return Collections.unmodifiableSet(this.configValidators);
    }

    /**
     * A functional interface for config validation.
     */
    @FunctionalInterface
    public interface ConfigValidator {

        /**
         * Validates the configuration of a task.
         *
         * @param taskConfig the configuration of the task
         * @param problems the set of problems to report validation issues
         */
        void validateConfig(final @NotNull Map<String, Object> taskConfig, final @NotNull Set<ConfigProblem> problems);
    }
}
