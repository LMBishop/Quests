package com.leonardobishop.quests.bukkit.tasktype.type.replenishing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class ReplenishingTask extends AmountTask {

    public static final TaskSchema<ReplenishingTask> SCHEMA = AmountTask.amountSchema(ReplenishingTask::new)
            .keys(BukkitTaskKeys.BLOCKS, BukkitTaskKeys.DATA, BukkitTaskKeys.MOBS)
            .build();

    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;
    public final @Nullable List<EntityType> mobs;

    public ReplenishingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
    }
}
