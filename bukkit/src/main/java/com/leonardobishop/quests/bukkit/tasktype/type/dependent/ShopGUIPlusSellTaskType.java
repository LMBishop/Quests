package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ShopGUIPlusSellTaskType extends ShopGUIPlusInteractionTaskType {

    public ShopGUIPlusSellTaskType(BukkitQuestsPlugin plugin, String shopGUIPlusVersion) {
        super(plugin, shopGUIPlusVersion, "shopguiplus_sell", TaskUtils.TASK_ATTRIBUTION_STRING, "Sell a given item to a ShopGUIPlus shop", "shopguiplus_sellcertain");
    }

    @Override
    public boolean isCorrectInteraction(ShopTransactionResult result) {
        ShopAction shopAction = result.getShopAction();
        return shopAction == ShopAction.SELL || shopAction == ShopAction.SELL_ALL;
    }
}
