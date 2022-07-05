package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class BukkitTaskType extends TaskType implements Listener {

    protected BukkitTaskTypeManager taskTypeManager;

    public BukkitTaskType(@NotNull String type, String author, String description) {
        super(type, author, description);
    }

    public BukkitTaskType(@NotNull String type) {
        super(type);
    }

    public final void debug(@NotNull String message, String questId, @NotNull UUID player) {
        taskTypeManager.sendDebug(message, super.getType(), questId, player);
    }

}
