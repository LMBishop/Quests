package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public abstract class Task {

    private final String id;
    private final String type;
    private final TaskOptions options;

    public Task(final String id, final String type, final TaskOptions options) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.options = Objects.requireNonNull(options, "options cannot be null");
    }

    /**
     * @return the id of the task
     */
    @Contract(pure = true)
    public final String getId() {
        return this.id;
    }

    /**
     * @return the configured (resolved) task type for this task
     */
    @Contract(pure = true)
    public final String getType() {
        return this.type;
    }

    /**
     * @return parsed options of this task
     */
    @Contract(pure = true)
    public final TaskOptions getOptions() {
        return this.options;
    }

    /**
     * Returns the goal of this task, used by the {@code {taskid:goal}} placeholders.
     *
     * @return the goal, or {@code "-"} if the task has no meaningful goal
     */
    @Contract(pure = true)
    public Object getGoal() {
        return "-";
    }
}
