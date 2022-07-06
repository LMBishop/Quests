package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ShopGUIPlusBuyCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ShopGUIPlusBuyCertainTaskType(BukkitQuestsPlugin plugin) {
        super("shopguiplus_buycertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Purchase a given item from a ShopGUI+ shop");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        List<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType())) {
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        }
    
        TaskUtils.configValidateExists(root + ".shop-id", config.get("shop-id"), problems, "shop-id", super.getType());
        TaskUtils.configValidateExists(root + ".item-id", config.get("item-id"), problems, "item-id", super.getType());
        
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void afterTransaction(ShopPostTransactionEvent event) {
        ShopTransactionResult result = event.getResult();
        ShopAction shopAction = result.getShopAction();
        if (shopAction != ShopAction.BUY) {
            return;
        }
    
        Player player = result.getPlayer();
        QPlayerManager playerManager = this.plugin.getPlayerManager();
        QPlayer qPlayer = playerManager.getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }
        
        ShopItem shopItem = result.getShopItem();
        Shop shop = shopItem.getShop();
        String shopId = shop.getId();
        String itemId = shopItem.getId();
        int amountBought = result.getAmount();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player bought item (shop = " + shopId + ", item id = " + itemId + ")", quest.getId(), task.getId(), player.getUniqueId());

            String taskShopId = (String) task.getConfigValue("shop-id");
            if (taskShopId == null || !taskShopId.equals(shopId)) {
                super.debug("Shop id does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            String taskItemId = (String) task.getConfigValue("item-id");
            if (taskItemId == null || !taskItemId.equals(itemId)) {
                super.debug("Item id does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int amountNeeded = (int) task.getConfigValue("amount");

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
            int newProgress = progress + amountBought;
            taskProgress.setProgress(newProgress);

            super.debug("Updating task progress (now " + newProgress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (newProgress >= amountNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(amountNeeded);
                taskProgress.setCompleted(true);
            }
        }
    }
}
