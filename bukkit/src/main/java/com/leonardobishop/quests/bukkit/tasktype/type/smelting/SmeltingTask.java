package com.leonardobishop.quests.bukkit.tasktype.type.smelting;

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

import java.util.List;

@NullMarked
public final class SmeltingTask extends AmountTask {

    public static final TaskKey<String> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedString(List.of("smoker", "blast_furnace", "furnace")));

    public static final TaskSchema<SmeltingTask> SCHEMA = AmountTask.amountSchema(SmeltingTask::new)
            .keys(BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH, MODE)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .build();

    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final @Nullable String mode;

    public SmeltingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.mode = options.getOrNull(MODE);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
    }
}
