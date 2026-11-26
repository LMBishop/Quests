package com.leonardobishop.quests.bukkit.tasktype.type.enchanting;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.bukkit.enchantments.Enchantment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class EnchantingTask extends AmountTask {

    public static final TaskKey<List<Enchantment>> ENCHANTMENTS = TaskKey.of("enchantment", BukkitTaskKeys.enchantments());
    public static final TaskKey<Integer> MIN_LEVEL = TaskKey.of("min-level", TaskKeyParsers.integerValuePositive());

    public static final TaskSchema<EnchantingTask> SCHEMA = AmountTask.amountSchema(EnchantingTask::new)
            .keys(BukkitTaskKeys.ITEM, BukkitTaskKeys.DATA, ENCHANTMENTS)
            .keys(MIN_LEVEL)
            .build();

    public final @Nullable QuestItem item;
    public final @Nullable List<Enchantment> enchantments;
    public final @Nullable Integer minLevel;

    public EnchantingTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.item = options.getOrNull(BukkitTaskKeys.ITEM);
        this.enchantments = options.getOrNull(ENCHANTMENTS);
        this.minLevel = options.getOrNull(MIN_LEVEL);
    }
}
