package com.leonardobishop.quests.bukkit.tasktype.type.blockshearing;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
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
public final class BlockshearingTask extends AmountTask {

    public static final TaskKey<Boolean> COUNT_SHEARS = TaskKey.of("count-shears", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<BlockshearingTask> SCHEMA = AmountTask.amountSchema(BlockshearingTask::new)
            .keys(BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.BLOCKS, COUNT_SHEARS)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, true))
            .build();

    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement} (option {@code damage}), for blocks {@code MATERIAL:data} in {@link #blocks}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable Integer data;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final @Nullable List<BlockSpec> blocks;
    public final boolean countShears;

    public BlockshearingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.countShears = options.get(COUNT_SHEARS);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
    }
}
