package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.win;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BedWars1058WinTask extends AmountTask {

    public static final TaskSchema<BedWars1058WinTask> SCHEMA = AmountTask.amountSchema(BedWars1058WinTask::new)
            .build();

    public BedWars1058WinTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
