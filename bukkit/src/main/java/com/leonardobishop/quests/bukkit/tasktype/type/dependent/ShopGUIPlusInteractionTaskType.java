package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ShopGUIPlusInteractionTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private Method getShopItemMethod;
    private Method getShopMethod;
    private Method getIdMethod;

    public ShopGUIPlusInteractionTaskType(BukkitQuestsPlugin plugin, @NotNull String type, String author, String description, String... aliases) {
        super(type, author, description, aliases);
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));

        try {
            Class<?> clazz = Class.forName("net.brcdev.shopgui.shop.ShopTransactionResult");
            this.getShopItemMethod = clazz.getDeclaredMethod("getShopItem");

            Class<?> returnType = this.getShopItemMethod.getReturnType();
            this.getShopMethod = returnType.getDeclaredMethod("getShop");
            this.getIdMethod = returnType.getDeclaredMethod("getId");

            return;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) { }

        plugin.getLogger().severe("Failed to register event handler for ShopGUIPlus task type!");
        plugin.getLogger().severe("ShopGUIPlus version detected: " + CompatUtils.getPluginVersion("ShopGUIPlus"));
    }

    public abstract boolean isCorrectInteraction(ShopTransactionResult result);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShopPostTransaction(ShopPostTransactionEvent event) {
        ShopTransactionResult result = event.getResult();
        if (result.getResult() != ShopTransactionResult.ShopTransactionResultType.SUCCESS) {
            return;
        }

        if (!isCorrectInteraction(result)) {
            return;
        }

        Player player = result.getPlayer();
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Shop shop;
        String itemId;
        String shopId;

        try {
            Object shopItem = getShopItemMethod.invoke(result);
            shop = (Shop) getShopMethod.invoke(shopItem);
            itemId = (String) getIdMethod.invoke(shopItem);
            if (shop != null) {
                shopId = shop.getId();
            } else {
                shopId = null;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            // It should never happen
            return;
        }

        int amountBought = result.getAmount();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player traded item (shop = " + shopId + ", item id = " + itemId + ", action = " + result.getShopAction().toString() + ")", quest.getId(), task.getId(), player.getUniqueId());

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

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amountNeeded);
        }
    }
}
