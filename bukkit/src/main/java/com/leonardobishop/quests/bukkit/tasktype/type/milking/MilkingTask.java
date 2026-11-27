package com.leonardobishop.quests.bukkit.tasktype.type.milking;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.util.List;

@NullMarked
public final class MilkingTask extends AmountTask {

    public static final TaskSchema<MilkingTask> SCHEMA = AmountTask.amountSchema(MilkingTask::new)
            .keys(BukkitTaskKeys.MOBS, BukkitTaskKeys.SPAWN_REASONS)
            .build();

    public final @Nullable List<EntityType> mobs;
    public final @Nullable List<CreatureSpawnEvent.SpawnReason> spawnReasons;

    public MilkingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.spawnReasons = options.getOrNull(BukkitTaskKeys.SPAWN_REASONS);
    }
}
