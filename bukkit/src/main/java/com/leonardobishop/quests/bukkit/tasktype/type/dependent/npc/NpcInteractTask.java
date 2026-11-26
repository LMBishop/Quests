package com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Task of {@link NpcInteractTaskType} implementations.
 *
 * @param <I> type of the NPC id used by the NPC plugin
 */
@NullMarked
public final class NpcInteractTask<I> extends BukkitTask {

    public static final TaskKey<String> NPC_NAME = TaskKey.of("npc-name", TaskKeyParsers.stringValue());
    public static final TaskKey<List<Integer>> INTEGER_NPC_IDS = TaskKey.of("npc-id", TaskKeyParsers.integerListNonNegative());
    public static final TaskKey<List<String>> STRING_NPC_IDS = TaskKey.of("npc-id", TaskKeyParsers.stringList());

    public static final TaskSchema<NpcInteractTask<Integer>> INTEGER_ID_SCHEMA = schema(INTEGER_NPC_IDS);
    public static final TaskSchema<NpcInteractTask<String>> STRING_ID_SCHEMA = schema(STRING_NPC_IDS);

    public final @Nullable List<I> npcIds;
    public final @Nullable String npcName;

    private NpcInteractTask(final String id, final String type, final TaskOptions options, final TaskKey<List<I>> npcIdsKey) {
        super(id, type, options);

        this.npcIds = options.getOrNull(npcIdsKey);
        this.npcName = options.getOrNull(NPC_NAME);
    }

    private static <I> TaskSchema<NpcInteractTask<I>> schema(final TaskKey<List<I>> npcIdsKey) {
        return BukkitTask.<NpcInteractTask<I>>schema((id, type, options) -> new NpcInteractTask<>(id, type, options, npcIdsKey))
                .keys(npcIdsKey, NPC_NAME)
                .validator(npcNameIgnoredValidator(npcIdsKey))
                .build();
    }

    static TaskSchema.Validator npcNameIgnoredValidator(final TaskKey<?> npcIdsKey) {
        return (options, problems) -> {
            if (options.has(NPC_NAME) && options.has(npcIdsKey)) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        "Both npc-name and npc-id is specified; npc-name will be ignored", null, NPC_NAME.getPath()));
            }
        };
    }
}
