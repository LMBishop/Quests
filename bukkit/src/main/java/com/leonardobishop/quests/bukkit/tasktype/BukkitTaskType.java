package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.event.Listener;

public abstract class BukkitTaskType extends TaskType implements Listener {

    public BukkitTaskType(String type, String author, String description) {
        super(type, author, description);
    }

    public BukkitTaskType(String type) {
        super(type);
    }

}
