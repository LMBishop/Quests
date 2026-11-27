package com.leonardobishop.quests.bukkit.tasktype.type.blockchanging;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class BlockchangingTask extends AmountTask {

    public static final TaskKey<List<BlockSpec>> FROMS = TaskKey.of("from", BukkitTaskKeys.blocks(BukkitTaskKeys.MaterialMode.BLOCK)).withAliases("froms");
    public static final TaskKey<List<BlockSpec>> TOS = TaskKey.of("to", BukkitTaskKeys.blocks(BukkitTaskKeys.MaterialMode.BLOCK)).withAliases("tos");

    public static final TaskSchema<BlockchangingTask> SCHEMA = AmountTask.amountSchema(BlockchangingTask::new)
            .keys(FROMS, TOS, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER, BukkitTaskKeys.CHECK_COREPROTECT, BukkitTaskKeys.CHECK_COREPROTECT_TIME)
            .build();

    public final @Nullable List<BlockSpec> froms;
    public final @Nullable List<BlockSpec> tos;
    public final @Nullable Integer data;
    public final boolean checkPlayerBlockTracker;
    public final boolean checkCoreProtect;
    public final int checkCoreProtectTime;

    public BlockchangingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.froms = options.getOrNull(FROMS);
        this.tos = options.getOrNull(TOS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.checkPlayerBlockTracker = options.get(BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER);
        this.checkCoreProtect = options.get(BukkitTaskKeys.CHECK_COREPROTECT);
        this.checkCoreProtectTime = options.get(BukkitTaskKeys.CHECK_COREPROTECT_TIME);
    }
}
