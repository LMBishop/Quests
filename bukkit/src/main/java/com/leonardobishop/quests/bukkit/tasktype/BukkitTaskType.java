package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BukkitTaskType<T extends BukkitTask> extends TaskType<T> implements Listener {

    protected BukkitTaskTypeManager taskTypeManager;

    public BukkitTaskType(final @NotNull String type, final @Nullable String author, final @Nullable String description, final @NotNull TaskSchema<T> schema, final @NotNull String @NotNull ... aliases) {
        super(type, author, description, schema, aliases);
    }

    public BukkitTaskType(final @NotNull String type, final @Nullable String author, final @Nullable String description, final @NotNull TaskSchema<T> schema) {
        super(type, author, description, schema);
    }

    public BukkitTaskType(final @NotNull String type, final @NotNull TaskSchema<T> schema) {
        super(type, schema);
    }

    public final void debug(final @NotNull String message, final @NotNull String questId, final @NotNull String taskId, final @NotNull UUID player) {
        this.taskTypeManager.sendDebug(message, this.type, questId, taskId, player);
    }
}
