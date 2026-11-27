package com.leonardobishop.quests.bukkit.tasktype.type.dependent.shopguiplus;

import com.leonardobishop.quests.bukkit.tasktype.AmountTask;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ShopGUIPlusInteractionTask extends AmountTask {

    public static final TaskKey<String> SHOP_ID = TaskKey.of("shop-id", TaskKeyParsers.stringValue());
    public static final TaskKey<String> ITEM_ID = TaskKey.of("item-id", TaskKeyParsers.stringValue());

    public static final TaskSchema<ShopGUIPlusInteractionTask> SCHEMA = AmountTask.amountSchema(ShopGUIPlusInteractionTask::new)
            .keys(SHOP_ID, ITEM_ID)
            .build();

    public final @Nullable String shopId;
    public final @Nullable String itemId;

    public ShopGUIPlusInteractionTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.shopId = options.getOrNull(SHOP_ID);
        this.itemId = options.getOrNull(ITEM_ID);
    }
}
