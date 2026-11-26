package com.leonardobishop.quests.bukkit.tasktype.type.bartering;

import com.leonardobishop.quests.bukkit.item.QuestItem;
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

    public static final TaskKey<QuestItem> INPUT = BukkitTaskKeys.questItem("input", "data");
    public static final TaskKey<Boolean> INPUT_EXACT_MATCH = TaskKey.of("input-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<QuestItem> OUTPUT = BukkitTaskKeys.questItem("output", "data");
    public static final TaskKey<Boolean> OUTPUT_EXACT_MATCH = TaskKey.of("output-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<BarteringMode> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedValue(BarteringMode.STRING_MODE_MAP)).withDefault(BarteringMode.OUTPUT);

    public static final TaskSchema<BarteringTask> SCHEMA = AmountTask.amountSchema(BarteringTask::new)
            .keys(INPUT, INPUT_EXACT_MATCH, OUTPUT)
            .keys(OUTPUT_EXACT_MATCH, BukkitTaskKeys.DATA, MODE)
            .build();

    public final @Nullable QuestItem input;
    public final boolean inputExactMatch;
    public final @Nullable QuestItem output;
    public final boolean outputExactMatch;
    public final BarteringMode mode;

    public BarteringTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.input = options.getOrNull(INPUT);
        this.inputExactMatch = options.get(INPUT_EXACT_MATCH);
        this.output = options.getOrNull(OUTPUT);
        this.outputExactMatch = options.get(OUTPUT_EXACT_MATCH);
        this.mode = options.get(MODE);
    }
}
