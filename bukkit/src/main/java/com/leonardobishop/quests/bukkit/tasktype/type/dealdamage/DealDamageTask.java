package com.leonardobishop.quests.bukkit.tasktype.type.dealdamage;

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
public final class DealDamageTask extends AmountTask {

    public static final TaskKey<Boolean> ALLOW_ONLY_CREATURES = TaskKey.of("allow-only-creatures", TaskKeyParsers.booleanValue()).withDefault(true);
    public static final TaskKey<ItemRequirement> PROJECTILE_ITEM_REQUIREMENT = BukkitTaskKeys.itemRequirement("projectile-item-requirement");
    /** @deprecated use {@link #PROJECTILE_ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> PROJECTILE_ITEM = BukkitTaskKeys.questItem("projectile-item", "data");
    /** @deprecated use {@link #PROJECTILE_ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> PROJECTILE_ITEM_EXACT_MATCH = TaskKey.of("projectile-item-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<DealDamageTask> SCHEMA = AmountTask.amountSchema(DealDamageTask::new)
            .keys(BukkitTaskKeys.MOBS, ALLOW_ONLY_CREATURES, BukkitTaskKeys.ITEM_REQUIREMENT)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .keys(PROJECTILE_ITEM_REQUIREMENT, PROJECTILE_ITEM, PROJECTILE_ITEM_EXACT_MATCH)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .validator(BukkitTaskKeys.legacyItem(PROJECTILE_ITEM, PROJECTILE_ITEM_EXACT_MATCH, PROJECTILE_ITEM_REQUIREMENT))
            .build();

    public final @Nullable List<EntityType> mobs;
    public final boolean allowOnlyCreatures;
    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final @Nullable ItemRequirement projectileItemRequirement;
    /** @deprecated use {@link #projectileItemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem projectileItem;
    /** @deprecated use {@link #projectileItemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean projectileItemExactMatch;

    public DealDamageTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.allowOnlyCreatures = options.get(ALLOW_ONLY_CREATURES);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.projectileItem = options.getOrNull(PROJECTILE_ITEM);
        this.projectileItemExactMatch = options.get(PROJECTILE_ITEM_EXACT_MATCH);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
        this.projectileItemRequirement = ItemRequirement.resolve(options.getOrNull(PROJECTILE_ITEM_REQUIREMENT), this.projectileItem, this.projectileItemExactMatch);
    }
}
