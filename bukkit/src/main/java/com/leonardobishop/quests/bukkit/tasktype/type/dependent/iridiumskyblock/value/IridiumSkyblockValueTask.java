package com.leonardobishop.quests.bukkit.tasktype.type.dependent.iridiumskyblock.value;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class IridiumSkyblockValueTask extends BukkitTask {

    public static final TaskKey<Integer> VALUE = TaskKey.of("value", TaskKeyParsers.integerValueNonNegative()).required();

    public static final TaskSchema<IridiumSkyblockValueTask> SCHEMA = BukkitTask.schema(IridiumSkyblockValueTask::new)
            .keys(VALUE)
            .build();

    public final int value;

    public IridiumSkyblockValueTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.value = options.get(VALUE);
    }

    @Override
    public Object getGoal() {
        return this.value;
    }
}
