package com.leonardobishop.quests.bukkit.tasktype.type.playtime;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlaytimeTask extends BukkitTask {

    public static final TaskKey<Integer> MINUTES = TaskKey.of("minutes", TaskKeyParsers.integerValuePositive()).required();
    public static final TaskKey<Boolean> IGNORE_AFK = TaskKey.of("ignore-afk", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<PlaytimeTask> SCHEMA = BukkitTask.schema(PlaytimeTask::new)
            .keys(MINUTES, IGNORE_AFK)
            .build();

    public final int minutes;
    public final boolean ignoreAfk;

    public PlaytimeTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.minutes = options.get(MINUTES);
        this.ignoreAfk = options.get(IGNORE_AFK);
    }

    @Override
    public Object getGoal() {
        return this.minutes;
    }
}
