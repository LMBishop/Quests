package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

/**
 * A task with a required integer {@code amount} goal.
 */
@NullMarked
public abstract class AmountTask extends BukkitTask {

    public static final TaskKey<Integer> AMOUNT = TaskKey.of("amount", TaskKeyParsers.integerValuePositive()).required();

    public final int amount;

    protected AmountTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.amount = options.get(AMOUNT);
    }

    /**
     * Starts a schema with the keys every Bukkit task accepts plus {@link #AMOUNT}.
     */
    public static <T extends AmountTask> TaskSchema.Builder<T> amountSchema(final TaskSchema.Factory<T> factory) {
        return BukkitTask.schema(factory).key(AMOUNT);
    }

    @Override
    public Object getGoal() {
        return this.amount;
    }
}
