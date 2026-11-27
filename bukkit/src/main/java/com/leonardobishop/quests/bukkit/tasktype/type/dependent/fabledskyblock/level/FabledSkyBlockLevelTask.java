package com.leonardobishop.quests.bukkit.tasktype.type.dependent.fabledskyblock.level;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FabledSkyBlockLevelTask extends BukkitTask {

    public static final TaskKey<Integer> LEVEL = TaskKey.of("level", TaskKeyParsers.integerValueNonNegative()).required();

    public static final TaskSchema<FabledSkyBlockLevelTask> SCHEMA = BukkitTask.schema(FabledSkyBlockLevelTask::new)
            .keys(LEVEL)
            .build();

    public final int level;

    public FabledSkyBlockLevelTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.level = options.get(LEVEL);
    }

    @Override
    public Object getGoal() {
        return this.level;
    }
}
