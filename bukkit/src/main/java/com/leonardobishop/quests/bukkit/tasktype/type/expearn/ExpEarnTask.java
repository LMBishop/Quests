package com.leonardobishop.quests.bukkit.tasktype.type.expearn;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ExpEarnTask extends AmountTask {

    public static final TaskSchema<ExpEarnTask> SCHEMA = AmountTask.amountSchema(ExpEarnTask::new)
            .build();

    public ExpEarnTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
