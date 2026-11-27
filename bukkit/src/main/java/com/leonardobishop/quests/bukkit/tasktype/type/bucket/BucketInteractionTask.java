package com.leonardobishop.quests.bukkit.tasktype.type.bucket;

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
public final class BucketInteractionTask extends AmountTask {

    public static final TaskKey<ItemRequirement> BUCKET_REQUIREMENT = BukkitTaskKeys.itemRequirement("bucket-requirement");
    /** @deprecated use {@link #BUCKET_REQUIREMENT}; this option will be removed in a future version. */
    @Deprecated
    public static final TaskKey<QuestItem> BUCKET = BukkitTaskKeys.questItem("bucket", "data");

    public static final TaskSchema<BucketInteractionTask> SCHEMA = AmountTask.amountSchema(BucketInteractionTask::new)
            .keys(BUCKET_REQUIREMENT, BUCKET, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH)
            .validator(BukkitTaskKeys.legacyItem(BUCKET, BukkitTaskKeys.EXACT_MATCH, BUCKET_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BUCKET_REQUIREMENT, false))
            .build();

    public final @Nullable ItemRequirement bucketRequirement;
    /** @deprecated use {@link #bucketRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem bucket;
    /** @deprecated use {@link #bucketRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;

    public BucketInteractionTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.bucket = options.getOrNull(BUCKET);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.bucketRequirement = ItemRequirement.resolve(options.getOrNull(BUCKET_REQUIREMENT), this.bucket, this.exactMatch);
    }
}
