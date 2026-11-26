package com.leonardobishop.quests.bukkit.tasktype.type.distancefrom;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DistancefromTask extends BukkitTask {

    public static final TaskKey<String> WORLD = TaskKey.of("world", TaskKeyParsers.stringValue()).required();
    public static final TaskKey<Integer> X = TaskKey.of("x", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> Y = TaskKey.of("y", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> Z = TaskKey.of("z", TaskKeyParsers.integerValue()).required();
    public static final TaskKey<Integer> DISTANCE = TaskKey.of("distance", TaskKeyParsers.integerValueNonNegative()).required();

    public static final TaskSchema<DistancefromTask> SCHEMA = BukkitTask.schema(DistancefromTask::new)
            .keys(WORLD, X, Y)
            .keys(Z, DISTANCE)
            .build();

    public final String world;
    public final int x;
    public final int y;
    public final int z;
    public final int distance;

    public DistancefromTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.world = options.get(WORLD);
        this.x = options.get(X);
        this.y = options.get(Y);
        this.z = options.get(Z);
        this.distance = options.get(DISTANCE);
    }
}
