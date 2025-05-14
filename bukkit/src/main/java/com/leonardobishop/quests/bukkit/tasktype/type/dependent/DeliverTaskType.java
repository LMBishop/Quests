package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class DeliverTaskType<T> extends BukkitTaskType {

    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public DeliverTaskType(String type, String author, String description) {
        super(type, author, description);

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
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "remove-items-when-complete"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "allow-partial-completion"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    public abstract List<T> getNPCId(Task task);

    public void checkInventory(Player player, T npcId, String npcName, long delay, BukkitQuestsPlugin plugin) {
        if (player.hasMetadata("NPC") || !player.isOnline()) return;
        plugin.getScheduler().runTaskLaterAtLocation(player.getLocation(), () -> checkInventory(player, npcId, npcName, plugin), delay);
    }

    public void checkInventory(Player player, T npcId, String npcName, BukkitQuestsPlugin plugin) {
        if (!player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        boolean nameCorrected = false;

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player clicked NPC", quest.getId(), task.getId(), player.getUniqueId());

            List<T> configNPCId = getNPCId(task);
            if (configNPCId != null) {
                if (!configNPCId.contains(npcId)) {
                    super.debug("NPC id " + npcId + " does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
                String configNPCName = (String) task.getConfigValue("npc-name");
                if (configNPCName != null) {
                    if (npcName == null) {
                        super.debug("NPC name is empty and does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    if (!nameCorrected) {
                        npcName = Chat.legacyStrip(Chat.legacyColor(npcName));
                        nameCorrected = true;
                    }

                    if (!configNPCName.equals(npcName)) {
                        super.debug("NPC name " + npcName + " does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }
                }
            }

            boolean allowPartial = TaskUtils.getConfigBoolean(task, "allow-partial-completion");

            QuestItem qi;
            if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                qi = fetchedItem;
            }

            boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
            int[] amountPerSlot = TaskUtils.getAmountsPerSlot(player, qi, exactMatch);
            super.debug("Player has " + amountPerSlot[36] + " of the required item", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (allowPartial) {
                int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                int total = Math.min(amountPerSlot[36], amount - progress);

                if (total == 0) {
                    continue;
                }

                // We must ALWAYS remove items if partial completion is allowed
                // https://github.com/LMBishop/Quests/issues/375
                TaskUtils.removeItemsInSlots(player, amountPerSlot, total);
                super.debug("Removing " + total + " items from inventory", quest.getId(), task.getId(), player.getUniqueId());

                progress += total;
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                }
            } else {
                int progress = Math.min(amountPerSlot[36], amount);
                taskProgress.setProgress(progress);
                super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (progress >= amount) {
                    taskProgress.setCompleted(true);
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());

                    boolean remove = TaskUtils.getConfigBoolean(task, "remove-items-when-complete");

                    if (remove) {
                        TaskUtils.removeItemsInSlots(player, amountPerSlot, progress);
                        super.debug("Removing items from inventory", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
