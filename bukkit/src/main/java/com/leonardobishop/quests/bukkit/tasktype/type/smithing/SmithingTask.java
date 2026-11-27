package com.leonardobishop.quests.bukkit.tasktype.type.smithing;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.requirement.ItemRequirement;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import com.leonardobishop.quests.common.versioning.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class SmithingTask extends AmountTask {

    public static final TaskKey<String> MODE = TaskKey.of("mode", TaskKeyParsers.acceptedString(List.of("any", "transform", "trim")));

    public static final TaskSchema<SmithingTask> SCHEMA = AmountTask.amountSchema(SmithingTask::new)
            .keys(BukkitTaskKeys.ITEM_REQUIREMENT, BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA)
            .keys(BukkitTaskKeys.EXACT_MATCH, MODE)
            .validator(BukkitTaskKeys.legacyItem(BukkitTaskKeys.ITEM, BukkitTaskKeys.EXACT_MATCH, BukkitTaskKeys.ITEM_REQUIREMENT))
            .validator(BukkitTaskKeys.legacyData(BukkitTaskKeys.DATA, BukkitTaskKeys.ITEM_REQUIREMENT, false))
            .build();

    public final @Nullable ItemRequirement itemRequirement;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final @Nullable QuestItem item;
    /** @deprecated use {@link #itemRequirement}; the legacy option will be removed in a future version. */
    @Deprecated
    public final boolean exactMatch;
    public final @Nullable String mode;

    public SmithingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.exactMatch = options.get(BukkitTaskKeys.EXACT_MATCH);
        this.mode = options.getOrNull(MODE);
        this.itemRequirement = ItemRequirement.resolve(options.getOrNull(BukkitTaskKeys.ITEM_REQUIREMENT), this.item, this.exactMatch);
    }

    /**
     * For clarity reasons we want the user to specify the mode on 1.20+.
     */
    private static void validateMode(final TaskOptions options, final List<ConfigProblem> problems) {
        if (options.has(MODE) || BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class).getServerVersion().isLowerThan(Version.V1_20)) {
            return;
        }

        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, "Expected at least one correct value for 'mode'", null, "mode"));
    }
}
