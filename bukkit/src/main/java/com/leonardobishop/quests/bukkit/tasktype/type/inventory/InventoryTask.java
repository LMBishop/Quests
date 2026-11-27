package com.leonardobishop.quests.bukkit.tasktype.type.inventory;

import java.util.Objects;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class InventoryTask extends AmountTask {

    /** @deprecated use {@link #ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> ITEM = TaskKey.of("item", BukkitTaskKeys.questItem("data"));
    public static final TaskKey<Boolean> REMOVE_ITEMS_WHEN_COMPLETE = TaskKey.of("remove-items-when-complete", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> ALLOW_PARTIAL_COMPLETION = TaskKey.of("allow-partial-completion", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<InventoryTask> SCHEMA = AmountTask.amountSchema(InventoryTask::new)
            .keys(BukkitTaskKeys.ITEM_REQUIREMENT, ITEM, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH, REMOVE_ITEMS_WHEN_COMPLETE, ALLOW_PARTIAL_COMPLETION)
            .validator(BukkitTaskKeys.legacyItem(ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .validator(BukkitTaskKeys.requireItem(ITEM, BukkitTaskKeys.ITEM_REQUIREMENT))
            .build();

    public final ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final boolean removeItemsWhenComplete;
    public final boolean allowPartialCompletion;

    public InventoryTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.removeItemsWhenComplete = options.get(REMOVE_ITEMS_WHEN_COMPLETE);
        this.allowPartialCompletion = options.get(ALLOW_PARTIAL_COMPLETION);
        this.itemRequirement = Objects.requireNonNull(ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch));
    }
}
