package com.leonardobishop.quests.bukkit.tasktype.type.dealdamage;

import com.leonardobishop.quests.bukkit.item.QuestItem;
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
    public static final TaskKey<QuestItem> PROJECTILE_ITEM = BukkitTaskKeys.questItem("projectile-item", "data");
    public static final TaskKey<Boolean> PROJECTILE_ITEM_EXACT_MATCH = TaskKey.of("projectile-item-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<DealDamageTask> SCHEMA = AmountTask.amountSchema(DealDamageTask::new)
            .keys(BukkitTaskKeys.MOBS, ALLOW_ONLY_CREATURES, BukkitTaskKeys.ITEM)
            .keys(BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH, PROJECTILE_ITEM)
            .keys(PROJECTILE_ITEM_EXACT_MATCH)
            .build();

    public final @Nullable List<EntityType> mobs;
    public final boolean allowOnlyCreatures;
    public final @Nullable QuestItem item;
    public final boolean exactMatch;
    public final @Nullable QuestItem projectileItem;
    public final boolean projectileItemExactMatch;

    public DealDamageTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.allowOnlyCreatures = options.get(ALLOW_ONLY_CREATURES);
        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.projectileItem = options.getOrNull(PROJECTILE_ITEM);
        this.projectileItemExactMatch = options.get(PROJECTILE_ITEM_EXACT_MATCH);
    }
}
