package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import net.brcdev.shopgui.shop.ShopTransactionResult;

public final class ShopGUIPlusSellTaskType extends ShopGUIPlusInteractionTaskType {

    public ShopGUIPlusSellTaskType(BukkitQuestsPlugin plugin) {
        super(plugin, "shopguiplus_sell", TaskUtils.TASK_ATTRIBUTION_STRING, "Sell a given item to a ShopGUIPlus shop", "shopguiplus_sellcertain");
    }

    @Override
    public boolean isCorrectInteraction(ShopTransactionResult result) {
        ShopAction shopAction = result.getShopAction();
        return shopAction == ShopAction.SELL || shopAction == ShopAction.SELL_ALL;
    }
}
