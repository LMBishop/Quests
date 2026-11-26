package com.leonardobishop.quests.bukkit.tasktype.type.blocklootdispensing;

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
public final class BlockLootdispensingTask extends AmountTask {

    public static final TaskKey<Boolean> COUNT_DISPENSES = TaskKey.of("count-dispenses", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<BlockLootdispensingTask> SCHEMA = AmountTask.amountSchema(BlockLootdispensingTask::new)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.BLOCKS)
            .keys(BukkitTaskKeys.DATA, COUNT_DISPENSES)
            .build();

    public final @Nullable QuestItem item;
    public final boolean exactMatch;
    public final @Nullable List<BlockSpec> blocks;
    public final @Nullable Integer data;
    public final boolean countDispenses;

    public BlockLootdispensingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.countDispenses = options.get(COUNT_DISPENSES);
    }
}
