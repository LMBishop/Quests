package com.leonardobishop.quests.bukkit.tasktype.type.bartering;

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
public final class BarteringTask extends AmountTask {

    public static final TaskKey<ItemRequirement> INPUT_REQUIREMENT = BukkitTaskKeys.itemRequirement("input-requirement");
    /** @deprecated use {@link #INPUT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> INPUT = BukkitTaskKeys.questItem("input", "data");
    /** @deprecated use {@link #INPUT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> INPUT_EXACT_MATCH = TaskKey.of("input-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<ItemRequirement> OUTPUT_REQUIREMENT = BukkitTaskKeys.itemRequirement("output-requirement");
    /** @deprecated use {@link #OUTPUT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> OUTPUT = BukkitTaskKeys.questItem("output", "data");
    /** @deprecated use {@link #OUTPUT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> OUTPUT_EXACT_MATCH = TaskKey.of("output-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<BarteringMode> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedValue(BarteringMode.STRING_MODE_MAP)).withDefault(BarteringMode.OUTPUT);

    public static final TaskSchema<BarteringTask> SCHEMA = AmountTask.amountSchema(BarteringTask::new)
            .keys(INPUT_REQUIREMENT, INPUT, BukkitTaskKeys.DATA)
            .keys(INPUT_EXACT_MATCH, OUTPUT_REQUIREMENT, OUTPUT)
            .keys(OUTPUT_EXACT_MATCH, MODE)
            .validator(BukkitTaskKeys.legacyItem(INPUT, INPUT_EXACT_MATCH, INPUT_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, INPUT_REQUIREMENT, false))
            .validator(BukkitTaskKeys.legacyItem(OUTPUT, OUTPUT_EXACT_MATCH, OUTPUT_REQUIREMENT))
            .build();

    public final @Nullable ItemRequirement inputRequirement;
    /** @deprecated use {@link #inputRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem input;
    /** @deprecated use {@link #inputRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean inputExactMatch;
    public final @Nullable ItemRequirement outputRequirement;
    /** @deprecated use {@link #outputRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem output;
    /** @deprecated use {@link #outputRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean outputExactMatch;
    public final BarteringMode mode;

    public BarteringTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.input = options.getOrNull(INPUT);
        this.inputExactMatch = options.get(INPUT_EXACT_MATCH);
        this.output = options.getOrNull(OUTPUT);
        this.outputExactMatch = options.get(OUTPUT_EXACT_MATCH);
        this.mode = options.get(MODE);
        this.inputRequirement = ItemRequirement.resolve(options.getOrNull(INPUT_REQUIREMENT), this.input, this.inputExactMatch);
        this.outputRequirement = ItemRequirement.resolve(options.getOrNull(OUTPUT_REQUIREMENT), this.output, this.outputExactMatch);
    }
}
