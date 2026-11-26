package com.leonardobishop.quests.bukkit.tasktype.type.hatching;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.util.List;

@NullMarked
public final class HatchingTask extends AmountTask {

    public static final TaskSchema<HatchingTask> SCHEMA = AmountTask.amountSchema(HatchingTask::new)
            .keys(BukkitTaskKeys.MOBS)
            .build();

    public final @Nullable List<EntityType> mobs;

    public HatchingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
    }
}
