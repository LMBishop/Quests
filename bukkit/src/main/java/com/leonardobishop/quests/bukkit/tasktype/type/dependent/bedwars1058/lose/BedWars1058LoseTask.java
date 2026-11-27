package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.lose;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BedWars1058LoseTask extends AmountTask {

    public static final TaskSchema<BedWars1058LoseTask> SCHEMA = AmountTask.amountSchema(BedWars1058LoseTask::new)
            .build();

    public BedWars1058LoseTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
