package com.leonardobishop.quests.bukkit.tasktype.type.blockfertilizing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class BlockfertilizingTask extends AmountTask {

    public static final TaskSchema<BlockfertilizingTask> SCHEMA = AmountTask.amountSchema(BlockfertilizingTask::new)
            .keys(BukkitTaskKeys.BLOCKS, BukkitTaskKeys.DATA)
            .build();

    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;

    public BlockfertilizingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
    }
}
