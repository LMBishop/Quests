package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Manages the registration and handling of task types within the quest system.
 * The TaskTypeManager stores all registered task types and associates individual quests with each task type.
 * Task types can only be registered when registrations are enabled, typically during the startup phase.
 * This ensures that quests are only registered to task types after all task types have been registered.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
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
     * @param exclusions a collection of task type names to exclude from registration; must not be null
     */
    public TaskTypeManager(final Collection<String> exclusions) {
        Objects.requireNonNull(exclusions, "exclusions cannot be null");

        this.exclusions = Set.copyOf(exclusions);
        this.taskTypes = new HashMap<>();
        this.aliases = new HashMap<>();
        this.registrationsOpen = true;
        this.registered = 0;
        this.skipped = 0;
        this.unsupported = 0;
    }

    /**
     * Constructs a TaskTypeManager with an empty set of exclusions.
     */
    public TaskTypeManager() {
        this(Set.of());
    }

    /**
     * Returns an unmodifiable set containing task type exclusions.
     *
     * @return an unmodifiable set of task type exclusions
     */
    @Contract(pure = true)
    public @Unmodifiable Set<String> getExclusions() {
        return this.exclusions;
    }

    /**
     * Returns an unmodifiable collection of registered {@link TaskType} instances.
     *
     * @return an unmodifiable collection of registered task types
     */
    @Contract(pure = true)
    public @UnmodifiableView Collection<TaskType> getTaskTypes() {
        return Collections.unmodifiableCollection(this.taskTypes.values());
    }

    /**
     * Retrieves a registered {@link TaskType} by its type name.
     *
     * @param type the type name of the task type to retrieve; must not be null
     * @return the registered task type, or null if not found
     */
    @Contract(pure = true)
    public @Nullable TaskType getTaskType(final String type) {
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
     * Resolves the actual name of a registered {@link TaskType}, considering aliases.
     *
     * @param type the type name or alias of the task type to resolve; must not be null
     * @return the actual name of the registered task type, or null if not found
     */
    @Contract(pure = true)
    public @Nullable String resolveTaskTypeName(final String type) {
        Objects.requireNonNull(type, "type cannot be null");

        return this.taskTypes.containsKey(type)
                ? type
                : this.aliases.get(type);
    }

    /**
     * Registers a {@link TaskType} with the task type manager.
     *
     * @param taskType the task type to be registered; must not be null
     * @return whether the task type was successfully registered
     */
    public boolean registerTaskType(final TaskType taskType) {
        Objects.requireNonNull(taskType, "taskType cannot be null");

        if (!this.registrationsOpen) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }

        final String type = taskType.getType();
        final List<String> aliases = taskType.getAliases();

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
     * Registers a {@link TaskType} using a supplier and compatibility checks.
     *
     * @param taskTypeSupplier       a supplier that provides the task type to register; must not be null
     * @param compatibilitySuppliers suppliers that check for task type compatibility; must not be null
     * @return whether the task type was successfully registered
     */
    public boolean registerTaskType(final Supplier<TaskType> taskTypeSupplier, final BooleanSupplier... compatibilitySuppliers) {
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
     * Registers a quest with its associated task types. This will register the quest to each task type it contains.
     *
     * @param quest the quest to register; must not be null
     */
    public void registerQuestTasksWithTaskTypes(final Quest quest) {
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
     * Resets all quest-to-task type registrations. This does not clear the task types registered with the task type manager.
     */
    public void resetTaskTypes() {
        for (final TaskType taskType : this.taskTypes.values()) {
            taskType.unregisterAll();
        }
    }

    /**
     * Checks if task type registrations are still open.
     *
     * @return true if registrations are open, false otherwise
     */
    @Contract(pure = true)
    public boolean areRegistrationsOpen() {
        return this.registrationsOpen;
    }

    /**
     * Closes the task type registrations. This is typically done after the startup phase.
     */
    public void closeRegistrations() {
        this.registrationsOpen = false;
    }

    /**
     * Returns the number of task types that have been registered.
     *
     * @return the count of registered task types
     */
    @Contract(pure = true)
    public int getRegistered() {
        return this.registered;
    }

    /**
     * Returns the number of task types that were skipped due to exclusions or name conflicts.
     *
     * @return the count of skipped task types
     */
    @Contract(pure = true)
    public int getSkipped() {
        return this.skipped;
    }

    /**
     * Returns the number of task types that were skipped due to failing to meet specified requirements.
     *
     * @return the count of unsupported task types
     */
    @Contract(pure = true)
    public int getUnsupported() {
        return this.unsupported;
    }

    /**
     * Sends a debug message.
     *
     * @param message          the debug message to send
     * @param taskType         the name of the task type associated with the message
     * @param questId          the ID of the quest associated with the message
     * @param taskId           the ID of the task associated with the message
     * @param associatedPlayer the UUID of the player associated with the message
     */
    public abstract void sendDebug(final String message, final String taskType, final String questId, final String taskId, final UUID associatedPlayer);
}
