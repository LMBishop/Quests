package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import net.brcdev.shopgui.shop.ShopTransactionResult;

public final class ShopGUIPlusBuyTaskType extends ShopGUIPlusInteractionTaskType {

    public ShopGUIPlusBuyTaskType(BukkitQuestsPlugin plugin) {
        super(plugin, "shopguiplus_buy", TaskUtils.TASK_ATTRIBUTION_STRING, "Purchase a given item from a ShopGUIPlus shop", "shopguiplus_buycertain");
    }

    @Override
    public boolean isCorrectInteraction(ShopTransactionResult result) {
        ShopAction shopAction = result.getShopAction();
        return shopAction == ShopAction.BUY;
    }
}
