package com.leonardobishop.quests.bukkit.tasktype.type.dependent.bedwars1058.finalkill;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BedWars1058FinalKillTask extends AmountTask {

    public static final TaskSchema<BedWars1058FinalKillTask> SCHEMA = AmountTask.amountSchema(BedWars1058FinalKillTask::new)
            .build();

    public BedWars1058FinalKillTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);
    }
}
