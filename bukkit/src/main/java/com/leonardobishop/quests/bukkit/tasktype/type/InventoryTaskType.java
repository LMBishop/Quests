package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class InventoryTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    
    public InventoryTaskType(BukkitQuestsPlugin plugin) {
        super("inventory", TaskUtils.TASK_ATTRIBUTION_STRING, "Obtain a set of items.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType())) {
            Object configBlock = config.get("item");
            if (configBlock instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) configBlock;
                String itemloc = "item";
                if (!section.contains("item")) {
                    itemloc = "type";
                }
                if (!section.contains(itemloc)) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(""), root + ".item.type"));
                } else {
                    String type = String.valueOf(section.get(itemloc));
                    if (!plugin.getItemGetter().isValidMaterial(type)) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                                ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(type), root + ".item." + itemloc));
                    }
                }
            } else {
                if (Material.getMaterial(String.valueOf(configBlock)) == null) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(String.valueOf(configBlock)), root + ".item.item"));
                }
            }
        }
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true, false, "data");
        TaskUtils.configValidateBoolean(root + ".remove-items-when-complete", config.get("remove-items-when-complete"), problems, true, "remove-items-when-complete", super.getType());
        TaskUtils.configValidateBoolean(root + ".update-progress", config.get("update-progress"), problems, true, "update-progress", super.getType());
        return problems;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> this.checkInventory(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryCloseEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory((Player) event.getPlayer()), 1L); //Still some work to do as it doesn't really work
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    Material material;
                    int amount = (int) task.getConfigValue("amount");
                    Object configBlock = task.getConfigValue("item");
                    Object configData = task.getConfigValue("data");
                    Object remove = task.getConfigValue("remove-items-when-complete");

                    ItemStack is;
                    if (configBlock instanceof ConfigurationSection) {
                        is = plugin.getItemStack("", (ConfigurationSection) configBlock);
                    } else {
                        material = Material.getMaterial(String.valueOf(configBlock));

                        if (material == null) {
                            continue;
                        }
                        if (configData != null) {
                            is = new ItemStack(material, 1, ((Integer) configData).shortValue());
                        } else {
                            is = new ItemStack(material, 1);
                        }
                    }

                    if (task.getConfigValue("update-progress") != null
                            && (Boolean) task.getConfigValue("update-progress")) {
                        int inInv = getAmount(player, is, amount);
                        if (taskProgress.getProgress() != null && (int) taskProgress.getProgress() != inInv) {
                            taskProgress.setProgress(inInv);
                        } else if (taskProgress.getProgress() == null) {
                            taskProgress.setProgress(inInv);
                        }
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

    private int getAmount(Player player, ItemStack is, int max) {
        if (is == null) {
            return 0;
        }
        int amount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null || !slot.isSimilar(is))
                continue;
            amount += slot.getAmount();
        }
        return Math.min(amount, max);
    }

}
