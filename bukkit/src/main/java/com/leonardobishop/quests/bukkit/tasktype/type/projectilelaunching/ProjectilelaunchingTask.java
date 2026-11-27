package com.leonardobishop.quests.bukkit.tasktype.type.projectilelaunching;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.util.List;

@NullMarked
public final class ProjectilelaunchingTask extends AmountTask {

    public static final TaskKey<List<EntityType>> PROJECTILES = TaskKey.of("projectile", BukkitTaskKeys.entityTypes()).withAliases("projectiles");

    public static final TaskSchema<ProjectilelaunchingTask> SCHEMA = AmountTask.amountSchema(ProjectilelaunchingTask::new)
            .keys(PROJECTILES)
            .build();

    public final @Nullable List<EntityType> projectiles;

    public ProjectilelaunchingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.projectiles = options.getOrNull(PROJECTILES);
    }
}
