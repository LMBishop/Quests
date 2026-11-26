package com.leonardobishop.quests.bukkit.tasktype.type.mobkilling;

import com.leonardobishop.quests.bukkit.item.QuestItem;
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
    public static final TaskKey<QuestItem> PROJECTILE_ITEM = BukkitTaskKeys.questItem("projectile-item", "data");
    public static final TaskKey<Boolean> PROJECTILE_ITEM_EXACT_MATCH = TaskKey.of("projectile-item-exact-match", TaskKeyParsers.booleanValue()).withDefault(true);

    public static final TaskSchema<MobkillingTask> SCHEMA = AmountTask.amountSchema(MobkillingTask::new)
            .keys(BukkitTaskKeys.MOBS, BukkitTaskKeys.SPAWN_REASONS, HOSTILE)
            .keys(NAMES, NAME_MATCH_MODE, BukkitTaskKeys.ITEM)
            .keys(BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH, PROJECTILE_ITEM)
            .keys(PROJECTILE_ITEM_EXACT_MATCH)
            .build();

    public final @Nullable List<EntityType> mobs;
    public final @Nullable List<CreatureSpawnEvent.SpawnReason> spawnReasons;
    public final @Nullable Boolean hostile;
    public final @Nullable List<String> names;
    public final TaskUtils.StringMatchMode nameMatchMode;
    public final @Nullable QuestItem item;
    public final boolean exactMatch;
    public final @Nullable QuestItem projectileItem;
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
    }
}
