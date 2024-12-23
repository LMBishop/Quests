package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.IntMath;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SmeltingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public SmeltingTaskType(BukkitQuestsPlugin plugin) {
        super("smelting", TaskUtils.TASK_ATTRIBUTION_STRING, "Smelt or cook a set amount of a item.", "smeltingcertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Arrays.asList(
                "smoker",
                "blast_furnace",
                "furnace"
        ), "mode"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        //noinspection DuplicatedCode
        if (!(event.getInventory() instanceof FurnaceInventory) || event.getRawSlot() != 2
                || (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                || event.getAction() == InventoryAction.NOTHING
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR && event.getClick() == ClickType.DOUBLE_CLICK && (event.getCursor() != null && event.getCursor().getType() != Material.AIR) && ((event.getCursor().getAmount() + event.getCurrentItem().getAmount() > event.getCursor().getMaxStackSize()) || !event.getCursor().isSimilar(event.getCurrentItem())) // https://github.com/LMBishop/Quests/issues/754 (does not apply to crafting items lol)
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD && event.getClick() == ClickType.NUMBER_KEY && !plugin.getVersionSpecificHandler().isHotbarMoveAndReaddSupported() // https://discord.com/channels/211910297810632704/510553623022010371/1011035743331819550
                || event.getAction() == InventoryAction.DROP_ONE_SLOT && event.getClick() == ClickType.DROP && (event.getCursor() != null && event.getCursor().getType() != Material.AIR) // https://github.com/LMBishop/Quests/issues/430
                || event.getAction() == InventoryAction.DROP_ALL_SLOT && event.getClick() == ClickType.CONTROL_DROP && (event.getCursor() != null && event.getCursor().getType() != Material.AIR) // https://github.com/LMBishop/Quests/issues/430
                || event.getAction() == InventoryAction.UNKNOWN && event.getClick() == ClickType.UNKNOWN // for better ViaVersion support
                || !(event.getWhoClicked() instanceof Player player)
                || plugin.getVersionSpecificHandler().isOffHandSwap(event.getClick()) && !plugin.getVersionSpecificHandler().isOffHandEmpty(player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        int eventAmount;
        if (event.getAction() == InventoryAction.DROP_ONE_SLOT) {
            eventAmount = 1;
        } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
            eventAmount = IntMath.divide(item.getAmount(), 2, RoundingMode.CEILING);
        } else {
            eventAmount = item.getAmount();
            if (event.isShiftClick() && event.getClick() != ClickType.CONTROL_DROP) { // https://github.com/LMBishop/Quests/issues/317
                eventAmount = Math.min(eventAmount, plugin.getVersionSpecificHandler().getAvailableSpace(player, item));
                if (eventAmount == 0) {
                    return;
                }
            }
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        final InventoryType inventoryType = event.getInventory().getType();
        final String inventoryTypeName = inventoryTypeNames.get(inventoryType);

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            String mode = (String) task.getConfigValue("mode");
            if (mode != null && !inventoryTypeName.equals(mode)) {
                super.debug("Specific mode is required, but the actual mode '" + inventoryType + "' does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            super.debug("Player smelted " + eventAmount + " of " + item.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (task.hasConfigKey("item")) {
                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
                if (!qi.compareItemStack(item, exactMatch)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
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

    private static final Map<InventoryType, String> inventoryTypeNames = new HashMap<>() {{
        for (final InventoryType inventoryType : InventoryType.values()) {
            final String name = inventoryType.name();
            final String nameLowerCase = name.toLowerCase(Locale.ROOT);
            this.put(inventoryType, nameLowerCase);
        }
    }};
}
