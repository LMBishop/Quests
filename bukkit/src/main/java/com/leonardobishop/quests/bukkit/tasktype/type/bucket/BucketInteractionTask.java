package com.leonardobishop.quests.bukkit.tasktype.type.bucket;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class BucketInteractionTask extends AmountTask {

    public static final TaskKey<QuestItem> BUCKET = BukkitTaskKeys.questItem("bucket", "data");

    public static final TaskSchema<BucketInteractionTask> SCHEMA = AmountTask.amountSchema(BucketInteractionTask::new)
            .keys(BUCKET, BukkitTaskKeys.DATA, BukkitTaskKeys.EXACT_MATCH)
            .build();

    public final @Nullable QuestItem bucket;
    public final boolean exactMatch;

    public BucketInteractionTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.bucket = options.getOrNull(BUCKET);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
    }
}
