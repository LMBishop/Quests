package com.leonardobishop.quests.bukkit.tasktype.type.dependent.pyrofishingpro.fishing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PyroFishingProFishingTask extends AmountTask {

    public static final TaskKey<Integer> FISH_NUMBER = TaskKey.of("fish-number", TaskKeyParsers.integerValue());
    public static final TaskKey<String> TIER = TaskKey.of("tier", TaskKeyParsers.stringValue());

    public static final TaskSchema<PyroFishingProFishingTask> SCHEMA = AmountTask.amountSchema(PyroFishingProFishingTask::new)
            .keys(FISH_NUMBER, TIER)
            .build();

    public final @Nullable Integer fishNumber;
    public final @Nullable String tier;

    public PyroFishingProFishingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.fishNumber = options.getOrNull(FISH_NUMBER);
        this.tier = options.getOrNull(TIER);
    }
}
