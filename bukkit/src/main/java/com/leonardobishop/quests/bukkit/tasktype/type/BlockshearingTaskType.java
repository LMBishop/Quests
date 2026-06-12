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
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class BlockshearingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public BlockshearingTaskType(BukkitQuestsPlugin plugin) {
        super("blockshearing", TaskUtils.TASK_ATTRIBUTION_STRING, "Shear a set amount of certain blocks.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
        super.addConfigValidator(TaskUtils.useMaterialListConfigValidator(this, TaskUtils.MaterialListConfigValidatorMode.BLOCK, "block", "blocks"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "count-shears"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShearBlock(PlayerShearBlockEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Block block = event.getBlock();
        List<ItemStack> drops = event.getDrops();

        if (drops.isEmpty()) {
            handle(player, qPlayer, block, null, 0);
        }

        for (int i = 0; i < drops.size(); i++) {
            handle(player, qPlayer, block, drops.get(i), i);
        }
    }

    private void handle(Player player, QPlayer qPlayer, Block block, ItemStack item, int i) {
        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player sheared a block, current block is " + block.getType(), quest.getId(), task.getId(), player.getUniqueId());

            Boolean countShears = (Boolean) task.getConfigValue("count-shears", true);
            final int amountToIncrease;

            // Just in case getConfigValue behavior changes (which is expected in the future)
            //noinspection PointlessBooleanExpression
            if (Boolean.TRUE.equals(countShears)) {
                if (i != 0) {
                    continue;
                }

                amountToIncrease = 1;
            } else {
                if (task.hasConfigKey("item")) {
                    if (item == null) {
                        super.debug("Specific item is required, dropped item is null; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    super.debug("Specific item is required; dropped item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

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
                    } else {
                        super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }

                amountToIncrease = item != null ? item.getAmount() : 0;
            }

            if (!TaskUtils.matchBlock(this, pendingTask, block, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, amountToIncrease);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
