package com.leonardobishop.quests.bukkit.tasktype.type.blockplace;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class BuildingTask extends AmountTask {

    public static final TaskKey<Boolean> REVERSE_IF_BROKEN = TaskKey.of("reverse-if-broken", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<BuildingTask> SCHEMA = AmountTask.amountSchema(BuildingTask::new)
            .keys(BukkitTaskKeys.BLOCKS, BukkitTaskKeys.DATA, REVERSE_IF_BROKEN)
            .keys(BukkitTaskKeys.ALLOW_NEGATIVE_PROGRESS)
            .build();

    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;
    public final boolean reverseIfBroken;
    public final boolean allowNegativeProgress;

    public BuildingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.reverseIfBroken = options.get(REVERSE_IF_BROKEN);
        this.allowNegativeProgress = options.get(BukkitTaskKeys.ALLOW_NEGATIVE_PROGRESS);
    }
}
