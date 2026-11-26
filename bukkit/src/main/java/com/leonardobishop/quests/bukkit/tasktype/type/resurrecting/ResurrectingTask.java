package com.leonardobishop.quests.bukkit.tasktype.type.resurrecting;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ResurrectingTask extends AmountTask {

    public static final TaskSchema<ResurrectingTask> SCHEMA = AmountTask.amountSchema(ResurrectingTask::new)
            .build();

    public ResurrectingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
