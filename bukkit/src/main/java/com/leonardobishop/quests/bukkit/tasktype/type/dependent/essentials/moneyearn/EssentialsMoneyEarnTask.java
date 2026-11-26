package com.leonardobishop.quests.bukkit.tasktype.type.dependent.essentials.moneyearn;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EssentialsMoneyEarnTask extends AmountTask {

    public static final TaskSchema<EssentialsMoneyEarnTask> SCHEMA = AmountTask.amountSchema(EssentialsMoneyEarnTask::new)
            .build();

    public EssentialsMoneyEarnTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
