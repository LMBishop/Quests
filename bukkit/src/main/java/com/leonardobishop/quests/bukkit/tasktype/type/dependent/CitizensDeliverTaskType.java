package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class CitizensDeliverTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public CitizensDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a NPC.");
        this.plugin = plugin;

        super.addConfigValidator((config, problems) -> {
            if (config.containsKey("npc-name") && config.containsKey("npc-id")) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        "Both npc-name and npc-id is specified; npc-name will be ignored", null, "npc-name"));
            }
        });
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "remove-items-when-complete"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-partial-completion"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCClick(NPCRightClickEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory(event.getClicker(), event.getNPC()), 1L);
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player, NPC npc) {
        if (player == null || !player.isOnline()) {
            return;
        }
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player clicked NPC", quest.getId(), task.getId(), player.getUniqueId());

            if (task.getConfigValue("npc-id") != null) {
                if (!task.getConfigValue("npc-id").equals(npc.getId())) {
                    super.debug("NPC id ('" + npc.getId() + "') does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else if (task.getConfigValue("npc-name") != null) {
                String npcName = Chat.legacyStrip(Chat.legacyColor(npc.getName()));
                super.debug("NPC name is required, current name = '" + npcName + "'", quest.getId(), task.getId(), player.getUniqueId());
                if (!Chat.legacyStrip(Chat.legacyColor(String.valueOf(task.getConfigValue("npc-name"))))
                        .equals(npcName)) {
                    super.debug("NPC name does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }
            int itemsNeeded = (int) task.getConfigValue("amount");
            boolean remove = TaskUtils.getConfigBoolean(task, "remove-items-when-complete");
            boolean allowPartial = TaskUtils.getConfigBoolean(task, "allow-partial-completion");

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            int progress = TaskUtils.getIntegerTaskProgress(taskProgress);

            int total;
            int[] amountPerSlot = TaskUtils.getAmountsPerSlot(player, qi);
            super.debug("Player has " + amountPerSlot[36] + " of the required item", quest.getId(), task.getId(), player.getUniqueId());

            if (allowPartial) {
                total = Math.min(amountPerSlot[36], itemsNeeded - progress);

                if (total == 0) {
                    continue;
                }

                // We must ALWAYS remove items if partial completion is allowed
                // https://github.com/LMBishop/Quests/issues/375
                TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());

                progress += total;
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + (progress) + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= itemsNeeded) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                }
            } else {
                total = Math.min(amountPerSlot[36], itemsNeeded);

                taskProgress.setProgress(total);
                if (total >= itemsNeeded) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());

                    if (remove) {
                        TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                        super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }
        }
    }

}
