package com.leonardobishop.quests.bukkit.tasktype.type.trading;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class TradingTask extends AmountTask {

    public static final TaskKey<ItemRequirement> FIRST_INGREDIENT_REQUIREMENT = BukkitTaskKeys.itemRequirement("first-ingredient-requirement");
    /** @deprecated use {@link #FIRST_INGREDIENT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> FIRST_INGREDIENT = BukkitTaskKeys.questItem("first-ingredient", "data");
    /** @deprecated use {@link #FIRST_INGREDIENT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> FIRST_INGREDIENT_EXACT_MATCH = TaskKey.of("first-ingredient-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<ItemRequirement> SECOND_INGREDIENT_REQUIREMENT = BukkitTaskKeys.itemRequirement("second-ingredient-requirement");
    /** @deprecated use {@link #SECOND_INGREDIENT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> SECOND_INGREDIENT = BukkitTaskKeys.questItem("second-ingredient", "data");
    /** @deprecated use {@link #SECOND_INGREDIENT_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> SECOND_INGREDIENT_EXACT_MATCH = TaskKey.of("second-ingredient-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<TradingMode> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedValue(TradingMode.STRING_MODE_MAP)).withDefault(TradingMode.RESULT);

    public static final TaskSchema<TradingTask> SCHEMA = AmountTask.amountSchema(TradingTask::new)
            .keys(BukkitTaskKeys.MOBS, BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM)
            .keys(BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH, FIRST_INGREDIENT_REQUIREMENT)
            .keys(FIRST_INGREDIENT, FIRST_INGREDIENT_EXACT_MATCH, SECOND_INGREDIENT_REQUIREMENT)
            .keys(SECOND_INGREDIENT, SECOND_INGREDIENT_EXACT_MATCH, MODE)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .validator(BukkitTaskKeys.legacyItem(FIRST_INGREDIENT, FIRST_INGREDIENT_EXACT_MATCH, FIRST_INGREDIENT_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyItem(SECOND_INGREDIENT, SECOND_INGREDIENT_EXACT_MATCH, SECOND_INGREDIENT_REQUIREMENT))
            .build();

    public final @Nullable List<EntityType> mobs;
    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final @Nullable ItemRequirement firstIngredientRequirement;
    /** @deprecated use {@link #firstIngredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem firstIngredient;
    /** @deprecated use {@link #firstIngredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean firstIngredientExactMatch;
    public final @Nullable ItemRequirement secondIngredientRequirement;
    /** @deprecated use {@link #secondIngredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem secondIngredient;
    /** @deprecated use {@link #secondIngredientRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean secondIngredientExactMatch;
    public final TradingMode mode;

    public TradingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.firstIngredient = options.getOrNull(FIRST_INGREDIENT);
        this.firstIngredientExactMatch = options.get(FIRST_INGREDIENT_EXACT_MATCH);
        this.secondIngredient = options.getOrNull(SECOND_INGREDIENT);
        this.secondIngredientExactMatch = options.get(SECOND_INGREDIENT_EXACT_MATCH);
        this.mode = options.get(MODE);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
        this.firstIngredientRequirement = ItemRequirement.resolve(options.getOrNull(FIRST_INGREDIENT_REQUIREMENT), this.firstIngredient, this.firstIngredientExactMatch);
        this.secondIngredientRequirement = ItemRequirement.resolve(options.getOrNull(SECOND_INGREDIENT_REQUIREMENT), this.secondIngredient, this.secondIngredientExactMatch);
    }
}
