package com.leonardobishop.quests.bukkit.tasktype.type.dependent.mythicmobs.killing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class MythicMobsKillingTask extends AmountTask {

    public static final TaskKey<List<String>> NAMES = TaskKey.of("name", TaskKeyParsers.stringList()).withAliases("names").required();
    public static final TaskKey<TaskUtils.StringMatchMode> NAME_MATCH_MODE = BukkitTaskKeys.matchMode("name-match-mode");
    public static final TaskKey<Integer> MIN_LEVEL = TaskKey.of("min-level", TaskKeyParsers.integerValue()).withDefault(-1);
    public static final TaskKey<Integer> LEVEL = TaskKey.of("level", TaskKeyParsers.integerValue()).withDefault(-1);

    public static final TaskSchema<MythicMobsKillingTask> SCHEMA = AmountTask.amountSchema(MythicMobsKillingTask::new)
            .keys(NAMES, NAME_MATCH_MODE, MIN_LEVEL)
            .keys(LEVEL)
            .build();

    public final List<String> names;
    public final TaskUtils.StringMatchMode nameMatchMode;
    public final int minLevel;
    public final int level;

    public MythicMobsKillingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.names = options.get(NAMES);
        this.nameMatchMode = options.get(NAME_MATCH_MODE);
        this.minLevel = options.get(MIN_LEVEL);
        this.level = options.get(LEVEL);
    }
}
