package com.leonardobishop.quests.bukkit.tasktype.type.dependent.placeholderapi.evaluate;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class PlaceholderAPIEvaluateTask extends BukkitTask {

    public static final TaskKey<String> PLACEHOLDER = TaskKey.of("placeholder", TaskKeyParsers.stringValue()).required();
    public static final TaskKey<String> EVALUATES = TaskKey.of("evaluates", TaskKeyParsers.stringValue()).required();
    public static final TaskKey<PlaceholderAPIEvaluateOperator> OPERATOR = TaskKey.of("operator", TaskKeyParsers.enumValue(PlaceholderAPIEvaluateOperator.class));
    public static final TaskKey<Integer> REFRESH_TICKS = TaskKey.of("refresh-ticks", TaskKeyParsers.integerValueNonNegative());
    public static final TaskKey<Boolean> ASYNC = TaskKey.of("async", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<PlaceholderAPIEvaluateTask> SCHEMA = BukkitTask.schema(PlaceholderAPIEvaluateTask::new)
            .keys(PLACEHOLDER, EVALUATES, OPERATOR)
            .keys(REFRESH_TICKS, ASYNC)
            .validator(PlaceholderAPIEvaluateTask::validateNumericEvaluates)
            .build();

    public final String placeholder;
    public final String evaluates;
    public final @Nullable PlaceholderAPIEvaluateOperator operator;
    /** {@link #evaluates} parsed as a number if an {@link #operator} is set, otherwise null. */
    public final @Nullable Double evaluatesNumber;
    public final @Nullable Integer refreshTicks;
    public final boolean async;

    public PlaceholderAPIEvaluateTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.placeholder = options.get(PLACEHOLDER);
        this.evaluates = options.get(EVALUATES);
        this.operator = options.getOrNull(OPERATOR);
        this.evaluatesNumber = this.operator != null ? parseDouble(this.evaluates) : null;
        this.refreshTicks = options.getOrNull(REFRESH_TICKS);
        this.async = options.get(ASYNC);
    }

    @Override
    public Object getGoal() {
        return this.evaluates;
    }

    private static void validateNumericEvaluates(final TaskOptions options, final List<ConfigProblem> problems) {
        if (!options.has(OPERATOR)) {
            return;
        }

        final String evalStr = options.get(EVALUATES);
        if (parseDouble(evalStr) == null) {
            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                    "Numeric operator specified, but placeholder evaluation '" + evalStr + "' is not numeric",
                    "A numeric operator was specified, but<br>" +
                            "the evaluation '" + evalStr + "' is not numeric<br>" +
                            "and cannot be parsed.",
                    EVALUATES.getPath()));
        }
    }

    private static @Nullable Double parseDouble(final String value) {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }
}
