package com.leonardobishop.quests.bukkit.tasktype.type.smelting;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class SmeltingTask extends AmountTask {

    public static final TaskKey<String> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedString(List.of("smoker", "blast_furnace", "furnace")));

    public static final TaskSchema<SmeltingTask> SCHEMA = AmountTask.amountSchema(SmeltingTask::new)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .keys(MODE)
            .build();

    public final @Nullable QuestItem item;
    public final boolean exactMatch;
    public final @Nullable String mode;

    public SmeltingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.mode = options.getOrNull(MODE);
    }
}
