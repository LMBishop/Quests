package com.leonardobishop.quests.bukkit.tasktype.type.dependent.pinataparty.hit;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PinataPartyHitTask extends AmountTask {

    public static final TaskSchema<PinataPartyHitTask> SCHEMA = AmountTask.amountSchema(PinataPartyHitTask::new)
            .build();

    public PinataPartyHitTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
