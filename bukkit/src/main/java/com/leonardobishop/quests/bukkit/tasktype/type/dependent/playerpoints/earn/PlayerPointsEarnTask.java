package com.leonardobishop.quests.bukkit.tasktype.type.dependent.playerpoints.earn;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayerPointsEarnTask extends AmountTask {

    public static final TaskSchema<PlayerPointsEarnTask> SCHEMA = AmountTask.amountSchema(PlayerPointsEarnTask::new)
            .build();

    public PlayerPointsEarnTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
