package com.leonardobishop.quests.bukkit.tasktype.type.dependent.essentials.balance;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EssentialsBalanceTask extends AmountTask {

    public static final TaskSchema<EssentialsBalanceTask> SCHEMA = AmountTask.amountSchema(EssentialsBalanceTask::new)
            .build();

    public EssentialsBalanceTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
