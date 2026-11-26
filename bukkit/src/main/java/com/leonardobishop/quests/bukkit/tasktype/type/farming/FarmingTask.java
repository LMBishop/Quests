package com.leonardobishop.quests.bukkit.tasktype.type.farming;

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
public final class FarmingTask extends AmountTask {

    public static final TaskKey<FarmingMode> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedValue(FarmingMode.STRING_MODE_MAP));

    public static final TaskSchema<FarmingTask> SCHEMA = AmountTask.amountSchema(FarmingTask::new)
            .keys(BukkitTaskKeys.BLOCKS, BukkitTaskKeys.DATA, BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER)
            .keys(BukkitTaskKeys.CHECK_COREPROTECT, BukkitTaskKeys.CHECK_COREPROTECT_TIME, MODE)
            .build();

    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;
    public final boolean checkPlayerBlockTracker;
    public final boolean checkCoreProtect;
    public final int checkCoreProtectTime;
    public final @Nullable FarmingMode mode;

    public FarmingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.checkPlayerBlockTracker = options.get(BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER);
        this.checkCoreProtect = options.get(BukkitTaskKeys.CHECK_COREPROTECT);
        this.checkCoreProtectTime = options.get(BukkitTaskKeys.CHECK_COREPROTECT_TIME);
        this.mode = options.getOrNull(MODE);
    }
}
