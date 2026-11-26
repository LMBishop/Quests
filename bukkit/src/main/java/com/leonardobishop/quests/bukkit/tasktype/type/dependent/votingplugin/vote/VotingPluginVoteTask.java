package com.leonardobishop.quests.bukkit.tasktype.type.dependent.votingplugin.vote;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class VotingPluginVoteTask extends AmountTask {

    public static final TaskSchema<VotingPluginVoteTask> SCHEMA = AmountTask.amountSchema(VotingPluginVoteTask::new)
            .build();

    public VotingPluginVoteTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
