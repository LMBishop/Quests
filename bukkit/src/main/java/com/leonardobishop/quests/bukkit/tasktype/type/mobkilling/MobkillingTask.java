package com.leonardobishop.quests.bukkit.tasktype.type.mobkilling;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class MobkillingTask extends AmountTask {

    public static final TaskKey<Boolean> HOSTILE = TaskKey.of("hostile", TaskKeyParsers.booleanValue());
    public static final TaskKey<List<String>> NAMES = TaskKey.of("name", TaskKeyParsers.stringList()).withAliases("names");
    public static final TaskKey<TaskUtils.StringMatchMode> NAME_MATCH_MODE = BukkitTaskKeys.matchMode("name-match-mode");
    public static final TaskKey<ItemRequirement> PROJECTILE_ITEM_REQUIREMENT = BukkitTaskKeys.itemRequirement("projectile-item-requirement");
    /** @deprecated use {@link #PROJECTILE_ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> PROJECTILE_ITEM = BukkitTaskKeys.questItem("projectile-item", "data");
    /** @deprecated use {@link #PROJECTILE_ITEM_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<Boolean> PROJECTILE_ITEM_EXACT_MATCH = TaskKey.of("projectile-item-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<MobkillingTask> SCHEMA = AmountTask.amountSchema(MobkillingTask::new)
            .keys(BukkitTaskKeys.MOBS, BukkitTaskKeys.SPAWN_REASONS, HOSTILE)
            .keys(NAMES, NAME_MATCH_MODE, BukkitTaskKeys.ITEM_REQUIREMENT)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .keys(PROJECTILE_ITEM_REQUIREMENT, PROJECTILE_ITEM, PROJECTILE_ITEM_EXACT_MATCH)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .validator(BukkitTaskKeys.legacyItem(PROJECTILE_ITEM, PROJECTILE_ITEM_EXACT_MATCH, PROJECTILE_ITEM_REQUIREMENT))
            .build();

    public final @Nullable List<EntityType> mobs;
    public final @Nullable List<CreatureSpawnEvent.SpawnReason> spawnReasons;
    public final @Nullable Boolean hostile;
    public final @Nullable List<String> names;
    public final TaskUtils.StringMatchMode nameMatchMode;
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

    public MobkillingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.spawnReasons = options.getOrNull(BukkitTaskKeys.SPAWN_REASONS);
        this.hostile = options.getOrNull(HOSTILE);
        this.names = options.getOrNull(NAMES);
        this.nameMatchMode = options.get(NAME_MATCH_MODE);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.projectileItem = options.getOrNull(PROJECTILE_ITEM);
        this.projectileItemExactMatch = options.get(PROJECTILE_ITEM_EXACT_MATCH);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
        this.projectileItemRequirement = ItemRequirement.resolve(options.getOrNull(PROJECTILE_ITEM_REQUIREMENT), this.projectileItem, this.projectileItemExactMatch);
    }
}
