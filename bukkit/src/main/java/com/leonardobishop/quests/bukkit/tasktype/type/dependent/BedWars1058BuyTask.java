package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public final class BedWars1058BuyTask extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058BuyTask(BukkitQuestsPlugin plugin) {
        super("bedwars1058_buy", TaskUtils.TASK_ATTRIBUTION_STRING, "Buy specific items from the BedWars1058 shop.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShopBuy(ShopBuyEvent event) {
        Player buyer = event.getBuyer();
        if (buyer == null) {
            return;
        }

        QPlayer qBuyer = plugin.getPlayerManager().getPlayer(buyer.getUniqueId());
        if (qBuyer == null) {
            return;
        }

        ICategoryContent boughtContent = event.getCategoryContent();
        if (boughtContent == null) {
            return;
        }

        String boughtIdentifier = boughtContent.getIdentifier();
        ItemStack boughtStack = boughtContent.getItemStack(buyer);
        String boughtMaterial = boughtStack != null ? boughtStack.getType().name() : null;

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(buyer, qBuyer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            String requiredId = (String) task.getConfigValue("item");
            if (requiredId == null) {
                continue;
            }

            boolean matched = false;

            if (requiredId.equalsIgnoreCase(boughtIdentifier)) {
                matched = true;
                super.debug("Matched identifier " + boughtIdentifier,
                        quest.getId(), task.getId(), buyer.getUniqueId());
            }

            if (!matched && requiredId.equalsIgnoreCase(boughtMaterial)) {
                matched = true;
                super.debug("Matched material " + boughtMaterial,
                        quest.getId(), task.getId(), buyer.getUniqueId());
            }

            if (!matched) {
                super.debug("Bought item (" + boughtIdentifier + "/" + boughtMaterial + ") does not match required " + requiredId,
                        quest.getId(), task.getId(), buyer.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            int amount = ((int) task.getConfigValue("amount"));

            super.debug("Progress " + progress + "/" + amount,
                    quest.getId(), task.getId(), buyer.getUniqueId());

            if (progress >= amount) {
                taskProgress.setCompleted(true);
                super.debug("Marking task as complete",
                        quest.getId(), task.getId(), buyer.getUniqueId());
            }

            TaskUtils.sendTrackAdvancement(buyer, quest, task, pendingTask, amount);
        }
    }
}
