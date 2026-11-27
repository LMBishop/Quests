package com.leonardobishop.quests.bukkit.tasktype.type.dependent.mythicmobs.dealdamage;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
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
public final class MythicMobsDealDamageTask extends AmountTask {

    public static final TaskKey<List<String>> NAMES = TaskKey.of("name", TaskKeyParsers.stringList()).withAliases("names");
    public static final TaskKey<TaskUtils.StringMatchMode> NAME_MATCH_MODE = BukkitTaskKeys.matchMode("name-match-mode");
    public static final TaskKey<Integer> MIN_LEVEL = TaskKey.of("min-level", TaskKeyParsers.integerValue()).withDefault(-1);
    public static final TaskKey<Integer> LEVEL = TaskKey.of("level", TaskKeyParsers.integerValue()).withDefault(-1);

    public static final TaskSchema<MythicMobsDealDamageTask> SCHEMA = AmountTask.amountSchema(MythicMobsDealDamageTask::new)
            .keys(NAMES, NAME_MATCH_MODE, MIN_LEVEL)
            .keys(LEVEL, BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM)
            .keys(BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .build();

    public final @Nullable List<String> names;
    public final TaskUtils.StringMatchMode nameMatchMode;
    public final int minLevel;
    public final int level;
    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;

    public MythicMobsDealDamageTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.names = options.getOrNull(NAMES);
        this.nameMatchMode = options.get(NAME_MATCH_MODE);
        this.minLevel = options.get(MIN_LEVEL);
        this.level = options.get(LEVEL);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
    }
}
