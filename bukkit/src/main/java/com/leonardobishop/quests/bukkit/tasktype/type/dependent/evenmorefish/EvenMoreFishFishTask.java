package com.leonardobishop.quests.bukkit.tasktype.type.dependent.evenmorefish;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class EvenMoreFishFishTask extends AmountTask {

    public static final TaskKey<List<String>> FISHES = TaskKey.of("fish", TaskKeyParsers.stringList()).withAliases("fishes");
    public static final TaskKey<TaskUtils.StringMatchMode> FISH_MATCH_MODE = BukkitTaskKeys.matchMode("fish-match-mode");
    public static final TaskKey<List<String>> RARITIES = TaskKey.of("rarity", TaskKeyParsers.stringList()).withAliases("rarities");
    public static final TaskKey<TaskUtils.StringMatchMode> RARITY_MATCH_MODE = BukkitTaskKeys.matchMode("rarity-match-mode");

    public static final TaskSchema<EvenMoreFishFishTask> SCHEMA = AmountTask.amountSchema(EvenMoreFishFishTask::new)
            .keys(FISHES, FISH_MATCH_MODE, RARITIES)
            .keys(RARITY_MATCH_MODE)
            .build();

    public final @Nullable List<String> fishes;
    public final TaskUtils.StringMatchMode fishMatchMode;
    public final @Nullable List<String> rarities;
    public final TaskUtils.StringMatchMode rarityMatchMode;

    public EvenMoreFishFishTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.fishes = options.getOrNull(FISHES);
        this.fishMatchMode = options.get(FISH_MATCH_MODE);
        this.rarities = options.getOrNull(RARITIES);
        this.rarityMatchMode = options.get(RARITY_MATCH_MODE);
    }
}
