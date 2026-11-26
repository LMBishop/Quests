package com.leonardobishop.quests.bukkit.tasktype.type.curing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CuringTask extends AmountTask {

    public static final TaskSchema<CuringTask> SCHEMA = AmountTask.amountSchema(CuringTask::new)
            .build();

    public CuringTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
