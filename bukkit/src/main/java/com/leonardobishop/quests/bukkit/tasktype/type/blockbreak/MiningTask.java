package com.leonardobishop.quests.bukkit.tasktype.type.blockbreak;

import com.leonardobishop.quests.bukkit.item.QuestItem;
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
public final class MiningTask extends AmountTask {

    public static final TaskKey<Boolean> REVERSE_IF_PLACED = TaskKey.of("reverse-if-placed", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> ALLOW_SILK_TOUCH = TaskKey.of("allow-silk-touch", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<MiningTask> SCHEMA = AmountTask.amountSchema(MiningTask::new)
            .keys(BukkitTaskKeys.BLOCKS, BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH)
            .keys(BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER, BukkitTaskKeys.CHECK_COREPROTECT, BukkitTaskKeys.CHECK_COREPROTECT_TIME)
            .keys(REVERSE_IF_PLACED, BukkitTaskKeys.ALLOW_NEGATIVE_PROGRESS, ALLOW_SILK_TOUCH)
            .build();

    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;
    public final @Nullable QuestItem item;
    public final boolean exactMatch;
    public final boolean checkPlayerBlockTracker;
    public final boolean checkCoreProtect;
    public final int checkCoreProtectTime;
    public final boolean reverseIfPlaced;
    public final boolean allowNegativeProgress;
    public final boolean allowSilkTouch;

    public MiningTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.checkPlayerBlockTracker = options.get(BukkitTaskKeys.CHECK_PLAYERBLOCKTRACKER);
        this.checkCoreProtect = options.get(BukkitTaskKeys.CHECK_COREPROTECT);
        this.checkCoreProtectTime = options.get(BukkitTaskKeys.CHECK_COREPROTECT_TIME);
        this.reverseIfPlaced = options.get(REVERSE_IF_PLACED);
        this.allowNegativeProgress = options.get(BukkitTaskKeys.ALLOW_NEGATIVE_PROGRESS);
        this.allowSilkTouch = options.get(ALLOW_SILK_TOUCH);
    }
}
