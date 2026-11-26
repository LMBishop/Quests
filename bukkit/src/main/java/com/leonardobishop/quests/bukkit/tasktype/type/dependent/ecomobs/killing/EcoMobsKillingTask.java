package com.leonardobishop.quests.bukkit.tasktype.type.dependent.ecomobs.killing;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class EcoMobsKillingTask extends AmountTask {

    public static final TaskKey<List<String>> IDS = TaskKey.of("id", TaskKeyParsers.stringList()).withAliases("ids").required();
    public static final TaskKey<TaskUtils.StringMatchMode> ID_MATCH_MODE = BukkitTaskKeys.matchMode("id-match-mode");

    public static final TaskSchema<EcoMobsKillingTask> SCHEMA = AmountTask.amountSchema(EcoMobsKillingTask::new)
            .keys(IDS, ID_MATCH_MODE)
            .build();

    public final List<String> ids;
    public final TaskUtils.StringMatchMode idMatchMode;

    public EcoMobsKillingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.ids = options.get(IDS);
        this.idMatchMode = options.get(ID_MATCH_MODE);
    }
}
