package com.leonardobishop.quests.bukkit.tasktype.type.shearing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.util.List;

@NullMarked
public final class ShearingTask extends AmountTask {

    public static final TaskKey<List<DyeColor>> COLORS = TaskKey.of("color", BukkitTaskKeys.dyeColors()).withAliases("colors");

    public static final TaskSchema<ShearingTask> SCHEMA = AmountTask.amountSchema(ShearingTask::new)
            .keys(BukkitTaskKeys.MOBS, COLORS)
            .build();

    public final @Nullable List<EntityType> mobs;
    public final @Nullable List<DyeColor> colors;

    public ShearingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.mobs = options.getOrNull(BukkitTaskKeys.MOBS);
        this.colors = options.getOrNull(COLORS);
    }
}
