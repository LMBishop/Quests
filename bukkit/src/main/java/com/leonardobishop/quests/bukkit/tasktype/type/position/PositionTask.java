package com.leonardobishop.quests.bukkit.tasktype.type.position;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PositionTask extends BukkitTask {

    public static final TaskKey<String> WORLD = TaskKey.of("world", TaskKeyParsers.stringValue());
    public static final TaskKey<Integer> X = TaskKey.of("x", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> Y = TaskKey.of("y", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> Z = TaskKey.of("z", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> DISTANCE_PADDING = TaskKey.of("distance-padding", TaskKeyParsers.integerValueNonNegative());

    public static final TaskSchema<PositionTask> SCHEMA = BukkitTask.schema(PositionTask::new)
            .keys(WORLD, X, Y)
            .keys(Z, DISTANCE_PADDING)
            .build();

    public final @Nullable String world;
    public final int x;
    public final int y;
    public final int z;
    public final @Nullable Integer distancePadding;

    public PositionTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.world = options.getOrNull(WORLD);
        this.x = options.get(X);
        this.y = options.get(Y);
        this.z = options.get(Z);
        this.distancePadding = options.getOrNull(DISTANCE_PADDING);
    }
}
