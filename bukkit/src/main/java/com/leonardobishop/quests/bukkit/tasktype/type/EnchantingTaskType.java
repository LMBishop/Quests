package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.List;

public final class EnchantingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public EnchantingTaskType(BukkitQuestsPlugin plugin) {
        super("enchanting", TaskUtils.TASK_ATTRIBUTION_STRING, "Enchant a certain amount of items.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useEnchantmentListConfigValidator(this, "enchantment"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "min-level"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e) {
        if (e.getEnchanter().hasMetadata("NPC")) return;

        Player player = e.getEnchanter();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player enchanted item", quest.getId(), task.getId(), player.getUniqueId());

            if (task.hasConfigKey("item")) {
                QuestItem qi = TaskUtils.getConfigQuestItem(task, "item", "data");
                if (!qi.getItemStack().getType().equals(e.getItem().getType())) {
                    super.debug("Item does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            boolean hasEnchantment = true;

            if (task.hasConfigKey("enchantment")) {
                hasEnchantment = false;
                List<String> enchantments = TaskUtils.getConfigStringList(task, "enchantment");
                for (String enchantment : enchantments) {
                    Enchantment enchantmentObject = Enchantment.getByName(enchantment);
                    if (enchantmentObject == null) {
                        super.debug("Enchantment '" + enchantment + "' does not exist, skipping...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }
                    if (e.getEnchantsToAdd().containsKey(enchantmentObject)) {
                        super.debug("Enchantments to add contains enchantment '" + enchantment + "'", quest.getId(), task.getId(), player.getUniqueId());
                        if (task.hasConfigKey("min-level")) {
                            int level = (int) task.getConfigValue("min-level");
                            super.debug("Minimum level of " + level + " is specified", quest.getId(), task.getId(), player.getUniqueId());
                            if (e.getEnchantsToAdd().get(enchantmentObject) >= level) {
                                hasEnchantment = true;
                                super.debug("Item has minimum required level", quest.getId(), task.getId(), player.getUniqueId());
                                break;
                            } else {
                                super.debug("Item does not have minimum level (level = " + e.getEnchantsToAdd().get(enchantmentObject) + ")", quest.getId(), task.getId(), player.getUniqueId());
                            }
                        } else {
                            hasEnchantment = true;
                            break;
                        }
                    } else {
                        super.debug("Enchantments to add does not contains enchantment '" + enchantment + "'", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }
            }

            if (!hasEnchantment) {
                super.debug("Applied enchantments does not contain any in required enchantments, skipping...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int enchantsNeeded = (int) task.getConfigValue("amount");

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= enchantsNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }
}
