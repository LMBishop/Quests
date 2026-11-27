package com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc;

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

import java.util.List;

/**
 * Task of {@link NpcDeliverTaskType} implementations.
 *
 * @param <I> type of the NPC id used by the NPC plugin
 */
@NullMarked
public final class NpcDeliverTask<I> extends AmountTask {

    /** @deprecated use {@link #ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> ITEM = TaskKey.of("item", BukkitTaskKeys.questItem("data"));
    public static final TaskKey<Boolean> REMOVE_ITEMS_WHEN_COMPLETE = TaskKey.of("remove-items-when-complete", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> ALLOW_PARTIAL_COMPLETION = TaskKey.of("allow-partial-completion", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<NpcDeliverTask<Integer>> INTEGER_ID_SCHEMA = schema(NpcInteractTask.INTEGER_NPC_IDS);
    public static final TaskSchema<NpcDeliverTask<String>> STRING_ID_SCHEMA = schema(NpcInteractTask.STRING_NPC_IDS);

    public final @Nullable List<I> npcIds;
    public final @Nullable String npcName;
    public final ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final boolean removeItemsWhenComplete;
    public final boolean allowPartialCompletion;

    private NpcDeliverTask(final String id, final String type, final TaskOptions options, final TaskKey<List<I>> npcIdsKey) {
        super(id, type, options);

        this.npcIds = options.getOrNull(npcIdsKey);
        this.npcName = options.getOrNull(NpcInteractTask.NPC_NAME);
        this.item = options.getOrNull(ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.removeItemsWhenComplete = options.get(REMOVE_ITEMS_WHEN_COMPLETE);
        this.allowPartialCompletion = options.get(ALLOW_PARTIAL_COMPLETION);
        this.itemRequirement = Objects.requireNonNull(ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch));
    }

    private static <I> TaskSchema<NpcDeliverTask<I>> schema(final TaskKey<List<I>> npcIdsKey) {
        return AmountTask.<NpcDeliverTask<I>>amountSchema((id, type, options) -> new NpcDeliverTask<>(id, type, options, npcIdsKey))
                .keys(npcIdsKey, NpcInteractTask.NPC_NAME, BukkitTaskKeys.ITEM_REQUIREMENT)
                .keys(ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
                .keys(REMOVE_ITEMS_WHEN_COMPLETE, ALLOW_PARTIAL_COMPLETION)
                .validator(BukkitTaskKeys.legacyItem(ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
                .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
                .validator(BukkitTaskKeys.requireItem(ITEM, BukkitTaskKeys.ITEM_REQUIREMENT))
                .build();
    }
}
