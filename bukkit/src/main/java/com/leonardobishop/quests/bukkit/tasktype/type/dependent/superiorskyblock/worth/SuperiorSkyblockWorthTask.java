package com.leonardobishop.quests.bukkit.tasktype.type.dependent.superiorskyblock.worth;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SuperiorSkyblockWorthTask extends BukkitTask {

    public static final TaskKey<Integer> WORTH = TaskKey.of("worth", TaskKeyParsers.integerValueNonNegative()).required();

    public static final TaskSchema<SuperiorSkyblockWorthTask> SCHEMA = BukkitTask.schema(SuperiorSkyblockWorthTask::new)
            .keys(WORTH)
            .build();

    public final int worth;

    public SuperiorSkyblockWorthTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.worth = options.get(WORTH);
    }

    @Override
    public Object getGoal() {
        return this.worth;
    }
}
