package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitTaskType extends TaskType implements Listener {

    public BukkitTaskType(@NotNull String type, String author, String description) {
        super(type, author, description);
    }

    public BukkitTaskType(@NotNull String type) {
        super(type);
    }

}
