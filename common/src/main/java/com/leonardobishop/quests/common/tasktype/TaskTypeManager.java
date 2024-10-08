package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The task type manager stores all registered task types and registers individual quests to each task type.
 * Task types can only be registered if registrations are enabled, which is typically only during start-up.
 * This is to ensure quests are only registered to task types when all task types have been registered first.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class TaskTypeManager {

    private final Set<String> exclusions;
    private final Map<String, TaskType> taskTypes;
    private final Map<String, String> aliases;
    private boolean registrationsOpen;
    private int registered;
    private int skipped;
    private int unsupported;

    /**
     * Constructs a TaskTypeManager with a specified set of exclusions.
     *
     * @param exclusions a set of task type names to exclude from registration
     */
    public TaskTypeManager(final @NotNull Set<String> exclusions) {
        Objects.requireNonNull(exclusions, "exclusions cannot be null");

        this.exclusions = exclusions;
        this.taskTypes = new HashMap<>();
        this.aliases = new HashMap<>();
        this.registrationsOpen = true;
        this.registered = 0;
        this.skipped = 0;
        this.unsupported = 0;
    }

    /**
     * Constructs a TaskTypeManager with an empty set of exclusions.
     * Use this constructor when no exclusions are needed.
     */
    public TaskTypeManager() {
        this(Set.of());
    }

    /**
     * Returns an immutable set containing all task type exclusions.
     *
     * @return immutable {@link Set} containing all task type exclusions
     */
    @SuppressWarnings("unused")
    public @NotNull Set<String> getExclusions() {
        return Collections.unmodifiableSet(this.exclusions);
    }

    /**
     * Returns an immutable collection containing all registered task types.
     *
     * @return immutable {@link Set} containing all registered {@link TaskType}
     */
    public @NotNull Collection<TaskType> getTaskTypes() {
        return Collections.unmodifiableCollection(this.taskTypes.values());
    }

    /**
     * Gets a registered task type by type.
     *
     * @param type the type to check
     * @return the {@link TaskType} if found, null otherwise
     */
    public @Nullable TaskType getTaskType(final @NotNull String type) {
        Objects.requireNonNull(type, "type cannot be null");

        final TaskType taskType = this.taskTypes.get(type);
        if (taskType != null) {
            return taskType;
        }

        final String aliasType = this.aliases.get(type);
        if (aliasType != null) {
            return this.taskTypes.get(aliasType);
        }

        return null;
    }

    /**
     * Gets the actual name of a task type, following aliases.
     *
     * @param type name of task type
     * @return the actual name of the task type, or null if not found
     */
    public @Nullable String resolveTaskTypeName(final @NotNull String type) {
        Objects.requireNonNull(type, "type cannot be null");

        return this.taskTypes.containsKey(type)
                ? type
                : this.aliases.get(type);
    }

    /**
     * Registers a task type with the task type manager.
     *
     * @param taskType the task type to register
     * @return true if the task type was successfully registered, false otherwise
     */
    public boolean registerTaskType(final @NotNull TaskType taskType) {
        Objects.requireNonNull(taskType, "taskType cannot be null");

        if (!this.registrationsOpen) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }

        final String type = taskType.getType();
        final Set<String> aliases = taskType.getAliases();

        if (this.exclusions.contains(type) || this.taskTypes.containsKey(type)
                || !Collections.disjoint(this.exclusions, aliases)
                || !Collections.disjoint(this.taskTypes.keySet(), aliases)
                || !Collections.disjoint(this.aliases.keySet(), aliases)) {
            this.skipped++;
            return false;
        }

        this.taskTypes.put(type, taskType);
        for (final String alias : taskType.getAliases()) {
            this.aliases.put(alias, type);
        }

        this.registered++;
        return true;
    }

    /**
     * Registers a task type with the task type manager using suppliers.
     *
     * @param taskTypeSupplier supplier of the task type to register
     * @param compatibilitySuppliers suppliers to check for task type compatibility
     * @return true if the task type was successfully registered, false otherwise
     */
    public boolean registerTaskType(final @NotNull Supplier<TaskType> taskTypeSupplier, final @NotNull BooleanSupplier @NotNull ... compatibilitySuppliers) {
        Objects.requireNonNull(taskTypeSupplier, "taskTypeSupplier cannot be null");
        Objects.requireNonNull(compatibilitySuppliers, "compatibilitySuppliers cannot be null");

        if (!this.registrationsOpen) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }

        for (final BooleanSupplier supplier : compatibilitySuppliers) {
            if (!supplier.getAsBoolean()) {
                this.unsupported++;
                return false;
            }
        }

        return this.registerTaskType(taskTypeSupplier.get());
    }

    /**
     * Closes the task type registrations. This is typically done after start-up.
     */
    public void closeRegistrations() {
        this.registrationsOpen = false;
    }

    /**
     * Checks if registrations are still open.
     *
     * @return true if registrations are open, false otherwise
     */
    public boolean areRegistrationsOpen() {
        return this.registrationsOpen;
    }

    /**
     * Returns the number of task types registered.
     *
     * @return number of task types registered
     */
    public int getRegistered() {
        return this.registered;
    }

    /**
     * Returns the number of task types skipped due to exclusions or name conflicts.
     *
     * @return number of task types skipped
     */
    public int getSkipped() {
        return this.skipped;
    }

    /**
     * Returns the number of task types skipped due to failing to meet specified requirements.
     *
     * @return number of task types skipped due to failing to meet specified requirements
     */
    public int getUnsupported() {
        return this.unsupported;
    }

    /**
     * Registers a quest with its task types. This will register the quest to each task type it contains.
     *
     * @param quest the quest to register
     */
    public void registerQuestTasksWithTaskTypes(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        if (this.registrationsOpen) {
            throw new IllegalStateException("Still accepting new task types (type registrations must be closed before registering quests)");
        }

        for (final Task task : quest.getTasks()) {
            final TaskType taskType = this.getTaskType(task.getType());

            if (taskType != null) {
                taskType.registerQuest(quest);
            }
        }
    }

    /**
     * Resets all quest to task type registrations. This does not clear the task types registered to the task type manager.
     */
    public void resetTaskTypes() {
        for (final TaskType taskType : this.taskTypes.values()) {
            taskType.unregisterAll();
        }
    }

    public abstract void sendDebug(final @NotNull String message, final @NotNull String taskType, final @NotNull String questId, final @NotNull String taskId, final @NotNull UUID associatedPlayer);
}
