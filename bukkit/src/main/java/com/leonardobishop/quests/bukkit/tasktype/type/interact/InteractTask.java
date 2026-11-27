package com.leonardobishop.quests.bukkit.tasktype.type.interact;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BlockSpec;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class InteractTask extends AmountTask {

    public static final TaskKey<List<Action>> ACTIONS = TaskKey.of("action", TaskKeyParsers.enumList(Action.class)).withAliases("actions");
    public static final TaskKey<List<EquipmentSlot>> HANDS = TaskKey.of("hand", TaskKeyParsers.enumList(EquipmentSlot.class)).withAliases("hands");
    public static final TaskKey<List<Event.Result>> USE_INTERACTED_BLOCK_RESULTS = TaskKey.of("use-interacted-block-result", TaskKeyParsers.enumList(Event.Result.class)).withAliases("use-interacted-block-results");
    public static final TaskKey<List<Event.Result>> USE_ITEM_IN_HAND_RESULTS = TaskKey.of("use-item-in-hand-result", TaskKeyParsers.enumList(Event.Result.class)).withAliases("use-item-in-hand-results");

    public static final TaskSchema<InteractTask> SCHEMA = AmountTask.amountSchema(InteractTask::new)
            .keys(BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.BLOCKS, ACTIONS)
            .keys(HANDS, USE_INTERACTED_BLOCK_RESULTS, USE_ITEM_IN_HAND_RESULTS)
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
    public final @Nullable List<Action> actions;
    public final @Nullable List<EquipmentSlot> hands;
    public final @Nullable List<Event.Result> useInteractedBlockResults;
    public final @Nullable List<Event.Result> useItemInHandResults;

    public InteractTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.data = options.getOrNull(BukkitTaskKeys.DATA);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.blocks = options.getOrNull(BukkitTaskKeys.BLOCKS);
        this.actions = options.getOrNull(ACTIONS);
        this.hands = options.getOrNull(HANDS);
        this.useInteractedBlockResults = options.getOrNull(USE_INTERACTED_BLOCK_RESULTS);
        this.useItemInHandResults = options.getOrNull(USE_ITEM_IN_HAND_RESULTS);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
    }
}
