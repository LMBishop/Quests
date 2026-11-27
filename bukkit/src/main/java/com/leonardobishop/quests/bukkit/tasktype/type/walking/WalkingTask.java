package com.leonardobishop.quests.bukkit.tasktype.type.walking;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@NullMarked
public final class WalkingTask extends BukkitTask {

    public static final TaskKey<Integer> DISTANCE = TaskKey.of("distance", TaskKeyParsers.integerValuePositive()).required();
    public static final TaskKey<Set<WalkingMode>> MODES = TaskKey.of("mode", TaskKeyParsers.list(TaskKeyParsers.acceptedValue(WalkingMode.STRING_MODE_MAP)).map(WalkingTask::toSet)).withDefault(Set.of());

    public static final TaskSchema<WalkingTask> SCHEMA = BukkitTask.schema(WalkingTask::new)
            .keys(DISTANCE, MODES)
            .build();

    public final int distance;
    public final Set<WalkingMode> modes;

    public WalkingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.distance = options.get(DISTANCE);
        this.modes = options.get(MODES);
    }

    @Override
    public Object getGoal() {
        return this.distance;
    }

    private static Set<WalkingMode> toSet(final List<WalkingMode> modes) {
        return modes.isEmpty() ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(modes));
    }
}
