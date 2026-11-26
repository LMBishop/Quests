package com.leonardobishop.quests.bukkit.tasktype.type.dependent.customfishing.fishing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class CustomFishingFishingTask extends AmountTask {

    public static final TaskKey<List<String>> LOOTS = TaskKey.of("loot", TaskKeyParsers.stringList()).withAliases("loots");
    public static final TaskKey<TaskUtils.StringMatchMode> LOOT_MATCH_MODE = BukkitTaskKeys.matchMode("loot-match-mode");
    public static final TaskKey<List<String>> GROUPS = TaskKey.of("group", TaskKeyParsers.stringList()).withAliases("groups");
    public static final TaskKey<TaskUtils.StringMatchMode> GROUP_MATCH_MODE = BukkitTaskKeys.matchMode("group-match-mode");

    public static final TaskSchema<CustomFishingFishingTask> SCHEMA = AmountTask.amountSchema(CustomFishingFishingTask::new)
            .keys(LOOTS, LOOT_MATCH_MODE, GROUPS)
            .keys(GROUP_MATCH_MODE)
            .build();

    public final @Nullable List<String> loots;
    public final TaskUtils.StringMatchMode lootMatchMode;
    public final @Nullable List<String> groups;
    public final TaskUtils.StringMatchMode groupMatchMode;

    public CustomFishingFishingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.loots = options.getOrNull(LOOTS);
        this.lootMatchMode = options.get(LOOT_MATCH_MODE);
        this.groups = options.getOrNull(GROUPS);
        this.groupMatchMode = options.get(GROUP_MATCH_MODE);
    }
}
