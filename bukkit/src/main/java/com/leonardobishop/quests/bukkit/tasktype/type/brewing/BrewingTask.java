package com.leonardobishop.quests.bukkit.tasktype.type.brewing;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class BrewingTask extends AmountTask {

    public static final TaskKey<QuestItem> INGREDIENT = BukkitTaskKeys.questItem("ingredient", "data");

    public static final TaskSchema<BrewingTask> SCHEMA = AmountTask.amountSchema(BrewingTask::new)
            .keys(INGREDIENT, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .build();

    public final @Nullable QuestItem ingredient;
    public final boolean exactMatch;

    public BrewingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.ingredient = options.getOrNull(INGREDIENT);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
    }
}
