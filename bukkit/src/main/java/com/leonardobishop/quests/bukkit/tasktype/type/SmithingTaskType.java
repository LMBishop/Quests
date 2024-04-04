package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class SmithingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public SmithingTaskType(BukkitQuestsPlugin plugin) {
        super("smithing", TaskUtils.TASK_ATTRIBUTION_STRING, "Smith a specific item.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));

        if (plugin.getVersionSpecificHandler().getMinecraftVersion() < 20) {
            return;
        }

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "mode"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Arrays.asList(
                "any", // for clarity reasons we want the user to specify the mode on 1.20+
                "transform",
                "trim"
        ), "mode"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmithItem(SmithItemEvent event) {
        //noinspection DuplicatedCode
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR
                || event.getAction() == InventoryAction.NOTHING
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD && event.getClick() == ClickType.NUMBER_KEY && !plugin.getVersionSpecificHandler().isHotbarMoveAndReaddSupported() // https://discord.com/channels/211910297810632704/510553623022010371/1011035743331819550
                || event.getAction() == InventoryAction.DROP_ONE_SLOT && event.getClick() == ClickType.DROP && (event.getCursor() != null && event.getCursor().getType() != Material.AIR) // https://github.com/LMBishop/Quests/issues/430
                || event.getAction() == InventoryAction.DROP_ALL_SLOT && event.getClick() == ClickType.CONTROL_DROP && (event.getCursor() != null && event.getCursor().getType() != Material.AIR) // https://github.com/LMBishop/Quests/issues/430
                || event.getAction() == InventoryAction.UNKNOWN && event.getClick() == ClickType.UNKNOWN // for better ViaVersion support
                || !(event.getWhoClicked() instanceof Player player)
                || plugin.getVersionSpecificHandler().isOffHandSwap(event.getClick()) && !plugin.getVersionSpecificHandler().isOffHandEmpty(player)) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        int eventAmount = item.getAmount();
        if (event.isShiftClick() && event.getClick() != ClickType.CONTROL_DROP) { // https://github.com/LMBishop/Quests/issues/317
            // https://cdn.discordapp.com/attachments/510553623022010371/1011483223446007849/unknown.png
            eventAmount *= getSmithItemMultiplier(event);
            eventAmount = Math.min(eventAmount, plugin.getVersionSpecificHandler().getAvailableSpace(player, item));
            if (eventAmount == 0) {
                return;
            }
        }

        final String recipeType = plugin.getVersionSpecificHandler().getSmithMode(event);

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            // null on versions lower than 1.20
            if (recipeType != null) {
                String mode = (String) task.getConfigValue("mode");

                if (!recipeType.equals(mode) && !"any".equals(mode)) {
                    super.debug("Specific mode is required, but the actual mode '" + recipeType + "' does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            if (task.hasConfigKey("item")) {
                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                super.debug("Player smithed " + eventAmount + " of " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

                boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
                if (!qi.compareItemStack(item, exactMatch)) {
                    super.debug("Item does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, eventAmount);
            super.debug("Updating task progress (now " + (progress + eventAmount) + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if ((int) taskProgress.getProgress() >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(amount);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }

    private int getSmithItemMultiplier(SmithItemEvent event) {
        int min = -1;
        for (ItemStack item : this.plugin.getVersionSpecificHandler().getSmithItems(event)) {
            if (item == null) {
                continue;
            }
            int amount = item.getAmount();
            if ((min < 0 && amount > 0) || (min > 0 && amount < min)) {
                min = amount;
            }
        }
        return Math.max(min, 1);
    }
}
