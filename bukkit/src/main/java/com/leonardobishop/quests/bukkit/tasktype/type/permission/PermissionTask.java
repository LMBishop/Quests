package com.leonardobishop.quests.bukkit.tasktype.type.permission;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PermissionTask extends BukkitTask {

    public static final TaskKey<String> PERMISSION = TaskKey.of("permission", TaskKeyParsers.stringValue()).required();

    public static final TaskSchema<PermissionTask> SCHEMA = BukkitTask.schema(PermissionTask::new)
            .keys(PERMISSION)
            .build();

    public final String permission;

    public PermissionTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.permission = options.get(PERMISSION);
    }
}
