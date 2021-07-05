package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The task type manager stores all registered task types and registers individual quests to each task type.
 * Task types can only be registered if registrations are enabled, which is typically only during start-up.
 * This is to ensure quests are only registered to task types when all task types have been registered first.
 */
public abstract class TaskTypeManager {

    private final ArrayList<TaskType> taskTypes = new ArrayList<>();
    private boolean allowRegistrations;

    public TaskTypeManager() {
        allowRegistrations = true;
    }

    public void closeRegistrations() {
        allowRegistrations = false;
    }

    public boolean areRegistrationsAccepted() {
        return allowRegistrations;
    }

    /**
     * @return immutable {@link List} containing all registered {@link TaskType}
     */
    public @NotNull List<TaskType> getTaskTypes() {
        return Collections.unmodifiableList(taskTypes);
    }

    /**
     * Resets all quest to task type registrations. This does not clear the task types registered to the task type manager.
     */
    public void resetTaskTypes() {
        for (TaskType taskType : taskTypes) {
            taskType.unregisterAll();
        }
    }

    /**
     * Register a task type with the task type manager.
     *
     * @param taskType the task type to register
     */
    public void registerTaskType(@NotNull TaskType taskType) {
        Objects.requireNonNull(taskType, "taskType cannot be null");

        if (!allowRegistrations) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }
        taskTypes.add(taskType);
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

        for (TaskType taskType : taskTypes) {
            if (taskType.getType().equalsIgnoreCase(type)) {
                return taskType;
            }
        }
        return null;
    }
}
