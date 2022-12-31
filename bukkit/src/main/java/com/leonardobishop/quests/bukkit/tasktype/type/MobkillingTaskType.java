package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MobkillingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public MobkillingTaskType(BukkitQuestsPlugin plugin) {
        super("mobkilling", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a entity type.", "mobkillingcertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useEntityListConfigValidator(this, "mob", "mobs"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "hostile"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
    }

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (mob == null || mob instanceof Player) {
            return;
        }

        if (killer == null) {
            return;
        }

        if (killer.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(killer, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed " + mob.getType(), quest.getId(), task.getId(), killer.getUniqueId());

            List<String> configEntities = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("mob") ? "mob" : "mobs");

            if (task.hasConfigKey("hostile")) {
                boolean hostile = TaskUtils.getConfigBoolean(task, "hostile");

                if (!hostile && !(mob instanceof Animals)) {
                    super.debug("Mob must be passive, but is hostile, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                } else if (hostile && !(mob instanceof Monster)) {
                    super.debug("Mob must be hostile, but is passive, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                }

            }

            if (!configEntities.isEmpty()) {
                super.debug("List of required names entities; mob type is " + mob.getType(), quest.getId(), task.getId(), killer.getUniqueId());

                boolean validMob = false;
                for (String entry : configEntities) {
                    super.debug("Checking against mob '" + entry + "'", quest.getId(), task.getId(), killer.getUniqueId());
                    try {
                        EntityType entity = EntityType.valueOf(entry);
                        if (mob.getType() == entity) {
                            super.debug("Mob is valid", quest.getId(), task.getId(), killer.getUniqueId());
                            validMob = true;
                            break;
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                if (!validMob) {
                    super.debug("Mob is not in list of required mobs, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                }
            }

            List<String> configNames = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("name") ? "name" : "names");

            if (!configNames.isEmpty()) {
                super.debug("List of required names exists; mob name is '" + Chat.legacyStrip(mob.getCustomName()) + "'", quest.getId(), task.getId(), killer.getUniqueId());

                boolean validName = false;
                for (String name : configNames) {
                    super.debug("Checking against name '" + name + "'", quest.getId(), task.getId(), killer.getUniqueId());
                    name = Chat.legacyColor(name);
                    if (mob.getCustomName() != null && !mob.getCustomName().equals(name)) {
                        super.debug("Mob has valid name", quest.getId(), task.getId(), killer.getUniqueId());
                        validName = true;
                        break;
                    }
                }

                if (!validName) {
                    super.debug("Mob name is not in list of valid name, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                }
            }

            if (task.hasConfigKey("item")) {
                ItemStack item = plugin.getVersionSpecificHandler().getItemInMainHand(killer);
                if (item == null) {
                    super.debug("Specific item is required, player has no item in hand; continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                }

                super.debug("Specific item is required; player held item is of type '" + item.getType() + "'", quest.getId(), task.getId(), killer.getUniqueId());

                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                if (!qi.compareItemStack(item)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                } else {
                    super.debug("Item matches required item", quest.getId(), task.getId(), killer.getUniqueId());
                }
            }

            int mobKillsNeeded = (int) task.getConfigValue("amount");

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

            if (progress >= mobKillsNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}
