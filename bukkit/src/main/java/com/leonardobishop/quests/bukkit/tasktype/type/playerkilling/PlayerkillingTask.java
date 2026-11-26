package com.leonardobishop.quests.bukkit.tasktype.type.playerkilling;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayerkillingTask extends AmountTask {

    public static final TaskSchema<PlayerkillingTask> SCHEMA = AmountTask.amountSchema(PlayerkillingTask::new)
            .build();

    public PlayerkillingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
