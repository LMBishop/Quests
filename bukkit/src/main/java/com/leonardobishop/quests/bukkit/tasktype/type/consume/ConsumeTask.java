package com.leonardobishop.quests.bukkit.tasktype.type.consume;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ConsumeTask extends AmountTask {

    public static final TaskSchema<ConsumeTask> SCHEMA = AmountTask.amountSchema(ConsumeTask::new)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .build();

    public final @Nullable QuestItem item;
    public final boolean exactMatch;

    public ConsumeTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
    }
}
