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

public final class ShopGUIPlusSellCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ShopGUIPlusSellCertainTaskType(BukkitQuestsPlugin plugin) {
        super("shopguiplus_sellcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Sell a given item to a ShopGUI+ shop");
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
        if (shopAction != ShopAction.SELL && shopAction != ShopAction.SELL_ALL) {
            return;
        }
    
        Player player = result.getPlayer();
        QPlayerManager playerManager = this.plugin.getPlayerManager();
        QPlayer qplayer = playerManager.getPlayer(player.getUniqueId());
        if (qplayer == null) {
            return;
        }
        
        World world = player.getWorld();
        String worldName = world.getName();
        
        ShopItem shopItem = result.getShopItem();
        Shop shop = shopItem.getShop();
        String shopId = shop.getId();
        String itemId = shopItem.getId();
        int amountSold = result.getAmount();
    
        List<Quest> registeredQuests = super.getRegisteredQuests();
        for (Quest quest : registeredQuests) {
            if (!qplayer.hasStartedQuest(quest)) {
                continue;
            }
    
            QuestProgressFile questProgressFile = qplayer.getQuestProgressFile();
            QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
    
            String questTypeName = super.getType();
            List<Task> taskList = quest.getTasksOfType(questTypeName);
            for (Task task : taskList) {
                if (!TaskUtils.validateWorld(worldName, task)) {
                    continue;
                }
                
                String taskId = task.getId();
                TaskProgress taskProgress = questProgress.getTaskProgress(taskId);
                if (taskProgress.isCompleted()) {
                    continue;
                }
                
                String taskShopId = (String) task.getConfigValue("shop-id");
                if (taskShopId == null || !taskShopId.equals(shopId)) {
                    continue;
                }
                
                String taskItemId = (String) task.getConfigValue("item-id");
                if (taskItemId == null || !taskItemId.equals(itemId)) {
                    continue;
                }
                
                int amountNeeded = (int) task.getConfigValue("amount");
                
                int progressAmount;
                Object progress = taskProgress.getProgress();
                if (progress == null) {
                    progressAmount = 0;
                } else {
                    progressAmount = (int) progress;
                }
                
                int newProgress = (progressAmount + amountSold);
                taskProgress.setProgress(newProgress);
                
                if (newProgress >= amountNeeded) {
                    taskProgress.setCompleted(true);
                }
            }
        }
    }
}
