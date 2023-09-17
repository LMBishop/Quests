package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The task type manager stores all registered task types and registers individual quests to each task type.
 * Task types can only be registered if registrations are enabled, which is typically only during start-up.
 * This is to ensure quests are only registered to task types when all task types have been registered first.
 */
public abstract class TaskTypeManager {

    private final Map<String, TaskType> taskTypes = new HashMap<>();
    private final Map<String, String> aliases = new HashMap<>();
    private final List<String> exclusions;
    private int skipped;
    private boolean allowRegistrations;

    public TaskTypeManager() {
        allowRegistrations = true;
        exclusions = new ArrayList<>();
    }

    public TaskTypeManager(List<String> exclusions) {
        allowRegistrations = true;
        this.exclusions = exclusions;
    }

    public void closeRegistrations() {
        allowRegistrations = false;
    }

    public boolean areRegistrationsAccepted() {
        return allowRegistrations;
    }

    /**
     * @return immutable {@link Set} containing all registered {@link TaskType}
     */
    public @NotNull Collection<TaskType> getTaskTypes() {
        return Collections.unmodifiableCollection(taskTypes.values());
    }

    /**
     * Resets all quest to task type registrations. This does not clear the task types registered to the task type manager.
     */
    public void resetTaskTypes() {
        for (TaskType taskType : taskTypes.values()) {
            taskType.unregisterAll();
        }
    }

    /**
     * Register a task type with the task type manager.
     *
     * @param taskType the task type to register
     */
    public boolean registerTaskType(@NotNull TaskType taskType) {
        Objects.requireNonNull(taskType, "taskType cannot be null");

        if (!allowRegistrations) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }

        if (exclusions.contains(taskType.getType()) || taskTypes.containsKey(taskType.getType())) {
            skipped++;
            return false;
        }

        taskTypes.put(taskType.getType(), taskType);
        for (String alias : taskType.getAliases()) {
            aliases.put(alias, taskType.getType());
        }

        return true;
    }

    /**
     * Register a task type with the task type manager.
     *
     * @param taskTypeSupplier supplier of the task type to register
     * @param compatibilitySuppliers suppliers to check for task type compatibility
     */
    public boolean registerTaskType(@NotNull Supplier<TaskType> taskTypeSupplier, @NotNull BooleanSupplier... compatibilitySuppliers) {
        Objects.requireNonNull(taskTypeSupplier, "taskTypeSupplier cannot be null");

        if (!allowRegistrations) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }

        for (BooleanSupplier supplier : compatibilitySuppliers) {
            if (!supplier.getAsBoolean()) {
                return false;
            }
        }

        return registerTaskType(taskTypeSupplier.get());
    }

    /**
     * Register a quest with its task types. This will register the quest to each task type it contains.
     *
     * @param quest the quest to register
     */
    public void registerQuestTasksWithTaskTypes(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        if (allowRegistrations) {
            throw new IllegalStateException("Still accepting new task types (type registrations must be closed before registering quests)");
        }
        for (Task task : quest.getTasks()) {
            TaskType t;
            if ((t = getTaskType(task.getType())) != null) {

                t.registerQuest(quest);
            }
        }
    }

    /**
     * Get a registered task type by type
     *
     * @param type the type to check
     * @return {@link TaskType}
     */
    public @Nullable TaskType getTaskType(@NotNull String type) {
        Objects.requireNonNull(type, "type cannot be null");

        TaskType taskType = taskTypes.get(type);
        if (taskType == null) {
            if (aliases.get(type) != null) {
                return taskTypes.get(aliases.get(type));
            }
        }
        return taskType;
    }

    /**
     * Get the actual name of a task type, following aliases
     *
     * @param taskType name of task type
     * @return actual name
     */
    public @Nullable String resolveTaskTypeName(@NotNull String taskType) {
        Objects.requireNonNull(taskType, "taskType cannot be null");

        if (taskTypes.containsKey(taskType)) {
            return taskType;
        }
        if (aliases.containsKey(taskType)) {
            return aliases.get(taskType);
        }
        return null;
    }

    /**
     * @return immutable {@link List} containing all task type exclusions
     */
    public @NotNull List<String> getExclusions() {
        return Collections.unmodifiableList(exclusions);
    }

    /**
     * @return number of task types skipped due to exclusions / name conflicts
     */
    public int getSkipped() {
        return skipped;
    }
}
