package com.leonardobishop.quests.bukkit.tasktype.type.dependent.nuvotifier.vote;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class NuVotifierVoteTask extends AmountTask {

    public static final TaskSchema<NuVotifierVoteTask> SCHEMA = AmountTask.amountSchema(NuVotifierVoteTask::new)
            .build();

    public NuVotifierVoteTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
