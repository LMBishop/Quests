package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class InventoryTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public InventoryTaskType() {
        super("inventory", "lmbishop", "Obtain a set of items.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of item to retrieve."));
        this.creatorConfigValues.add(new ConfigValue("item", true, "Name or ID of item."));
        this.creatorConfigValues.add(new ConfigValue("remove-items-when-complete", false, "Take the items away from the player on completion (true/false, " +
                "default = false)."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkInventory(event.getPlayer());
            }
        }.runTaskLater(Quests.getInstance(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryInteractEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkInventory((Player) event.getWhoClicked());
            }
        }.runTaskLater(Quests.getInstance(), 1L);
    }

    private void checkInventory(Player player) {
        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    Material material;
                    int amount = (int) task.getConfigValue("amount");
                    Object configBlock = task.getConfigValue("item");
                    Object configData = task.getConfigValue("data");
                    Object remove = task.getConfigValue("remove-items-when-complete");

                    if (StringUtils.isNumeric(String.valueOf(configBlock))) {
                        material = Material.getMaterial((int) configBlock);
                    } else {
                        material = Material.getMaterial(String.valueOf(configBlock));
                    }

                    if (material == null) {
                        continue;
                    }
                    ItemStack is;
                    if (configData != null) {
                        is = new ItemStack(material, 1, ((Integer) configData).shortValue());
                    } else {
                        is = new ItemStack(material, 1);
                    }

                    if (player.getInventory().containsAtLeast(is, amount)) {
                        is.setAmount(amount);
                        taskProgress.setCompleted(true);

                        if (remove != null && ((Boolean) remove)) {
                            player.getInventory().removeItem(is);
                        }
                    }
                }
            }
        }
    }

}
