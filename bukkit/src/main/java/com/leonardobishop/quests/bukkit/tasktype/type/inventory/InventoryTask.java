package com.leonardobishop.quests.bukkit.tasktype.type.inventory;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class InventoryTask extends AmountTask {

    public static final TaskKey<QuestItem> ITEM = TaskKey.of("item", BukkitTaskKeys.questItem("data")).required();
    public static final TaskKey<Boolean> REMOVE_ITEMS_WHEN_COMPLETE = TaskKey.of("remove-items-when-complete", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> ALLOW_PARTIAL_COMPLETION = TaskKey.of("allow-partial-completion", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<InventoryTask> SCHEMA = AmountTask.amountSchema(InventoryTask::new)
            .keys(ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .keys(REMOVE_ITEMS_WHEN_COMPLETE, ALLOW_PARTIAL_COMPLETION)
            .build();

    public final QuestItem item;
    public final boolean exactMatch;
    public final boolean removeItemsWhenComplete;
    public final boolean allowPartialCompletion;

    public InventoryTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.get(ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.removeItemsWhenComplete = options.get(REMOVE_ITEMS_WHEN_COMPLETE);
        this.allowPartialCompletion = options.get(ALLOW_PARTIAL_COMPLETION);
    }
}
