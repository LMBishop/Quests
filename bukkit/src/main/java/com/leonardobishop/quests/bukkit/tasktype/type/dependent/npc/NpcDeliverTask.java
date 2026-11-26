package com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc;

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

/**
 * Task of {@link NpcDeliverTaskType} implementations.
 *
 * @param <I> type of the NPC id used by the NPC plugin
 */
@NullMarked
public final class NpcDeliverTask<I> extends AmountTask {

    public static final TaskKey<QuestItem> ITEM = TaskKey.of("item", BukkitTaskKeys.questItem("data")).required();
    public static final TaskKey<Boolean> REMOVE_ITEMS_WHEN_COMPLETE = TaskKey.of("remove-items-when-complete", TaskKeyParsers.booleanValue()).withDefault(false);
    public static final TaskKey<Boolean> ALLOW_PARTIAL_COMPLETION = TaskKey.of("allow-partial-completion", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<NpcDeliverTask<Integer>> INTEGER_ID_SCHEMA = schema(NpcInteractTask.INTEGER_NPC_IDS);
    public static final TaskSchema<NpcDeliverTask<String>> STRING_ID_SCHEMA = schema(NpcInteractTask.STRING_NPC_IDS);

    public final @Nullable List<I> npcIds;
    public final @Nullable String npcName;
    public final QuestItem item;
    public final boolean exactMatch;
    public final boolean removeItemsWhenComplete;
    public final boolean allowPartialCompletion;

    private NpcDeliverTask(final String id, final String type, final TaskOptions options, final TaskKey<List<I>> npcIdsKey) {
        super(id, type, options);

        this.npcIds = options.getOrNull(npcIdsKey);
        this.npcName = options.getOrNull(NpcInteractTask.NPC_NAME);
        this.item = options.get(ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.removeItemsWhenComplete = options.get(REMOVE_ITEMS_WHEN_COMPLETE);
        this.allowPartialCompletion = options.get(ALLOW_PARTIAL_COMPLETION);
    }

    private static <I> TaskSchema<NpcDeliverTask<I>> schema(final TaskKey<List<I>> npcIdsKey) {
        return AmountTask.<NpcDeliverTask<I>>amountSchema((id, type, options) -> new NpcDeliverTask<>(id, type, options, npcIdsKey))
                .keys(npcIdsKey, NpcInteractTask.NPC_NAME)
                .keys(ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
                .keys(REMOVE_ITEMS_WHEN_COMPLETE, ALLOW_PARTIAL_COMPLETION)
                .validator(NpcInteractTask.npcNameIgnoredValidator(npcIdsKey))
                .build();
    }
}
