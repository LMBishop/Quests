package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class InteractTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public InteractTaskType(BukkitQuestsPlugin plugin) {
        super("interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with an item a certain amount of times.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        Player player = event.getPlayer();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player interacted", quest.getId(), task.getId(), player.getUniqueId());

            List<String> configBlocks = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("mob") ? "mob" : "mobs");

            if (task.hasConfigKey("item")) {
                ItemStack held = event.getItem();
                if (held == null) {
                    super.debug("Item is required, current item is null", quest.getId(), task.getId(), player.getUniqueId());
                }
                super.debug("Item is required, current item is " + held.getType(), quest.getId(), task.getId(), player.getUniqueId());
                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }
                if (!qi.compareItemStack(held)) {
                    super.debug("Item is not the required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Item match", quest.getId(), task.getId(), player.getUniqueId());
                }
            }

            if (!configBlocks.isEmpty()) {
                Block block = event.getClickedBlock();
                if (block == null) {
                    super.debug("Clicked block is required, current clicked block is null", quest.getId(), task.getId(), player.getUniqueId());
                }
                super.debug("Clicked block is required, current clicked block is " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());
                if (TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                    super.debug("Block match", quest.getId(), task.getId(), player.getUniqueId());
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int breedingNeeded = (int) task.getConfigValue("amount");

            if (progress >= breedingNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}