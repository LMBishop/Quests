package com.leonardobishop.quests.bukkit.tasktype.type.brewing;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class BrewingTask extends AmountTask {

    public static final TaskKey<ItemRequirement> INGREDIENT_REQUIREMENT = BukkitTaskKeys.itemRequirement("ingredient-requirement");
    /** @deprecated use {@link #INGREDIENT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> INGREDIENT = BukkitTaskKeys.questItem("ingredient", "data");

    public static final TaskSchema<BrewingTask> SCHEMA = AmountTask.amountSchema(BrewingTask::new)
            .keys(INGREDIENT_REQUIREMENT, INGREDIENT, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH)
            .validator(BukkitTaskKeys.legacyItem(INGREDIENT, BukkitTaskKeys.EXACT_MATCH, INGREDIENT_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, INGREDIENT_REQUIREMENT, false))
            .build();

    public final @Nullable ItemRequirement ingredientRequirement;
    /** @deprecated use {@link #ingredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem ingredient;
    /** @deprecated use {@link #ingredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;

    public BrewingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.ingredient = options.getOrNull(INGREDIENT);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.ingredientRequirement = ItemRequirement.resolve(options.getOrNull(INGREDIENT_REQUIREMENT), this.ingredient, this.exactMatch);
    }
}
