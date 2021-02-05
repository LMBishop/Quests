package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class InventoryTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public InventoryTaskType() {
        super("inventory", "LMBishop", "Obtain a set of items.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of item to retrieve."));
        this.creatorConfigValues.add(new ConfigValue("item", true, "Name or ID of item."));
        this.creatorConfigValues.add(new ConfigValue("data", false, "Data of item."));
        this.creatorConfigValues.add(new ConfigValue("remove-items-when-complete", false, "Take the items away from the player on completion (true/false, " +
                "default = false)."));
        this.creatorConfigValues.add(new ConfigValue("update-progress", false, "Update the displayed progress (if this causes lag then disable it)."));
        this.creatorConfigValues.add(new ConfigValue("worlds", false, "Permitted worlds the player must be in."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType())) {
            Object configBlock = config.get("item");
            if (configBlock instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) configBlock;
                String itemloc = "item";
                if (!section.contains("item")) {
                    itemloc = "type";
                }
                if (!section.contains(itemloc)) {
                    problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                            QuestsConfigLoader.ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(""), root + ".item.type"));
                } else {
                    String type = String.valueOf(section.get(itemloc));
                    if (!Quests.get().getItemGetter().isValidMaterial(type)) {
                        problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                                QuestsConfigLoader.ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(type), root + ".item." + itemloc));
                    }
                }
            } else {
                if (Material.getMaterial(String.valueOf(configBlock)) == null) {
                    problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                            QuestsConfigLoader.ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(String.valueOf(configBlock)), root + ".item.item"));
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


    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Bukkit.getScheduler().runTaskLater(Quests.get(), () -> this.checkInventory(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryCloseEvent event) {
        Bukkit.getScheduler().runTaskLater(Quests.get(), () -> checkInventory((Player) event.getPlayer()), 1L); //Still some work to do as it doesn't really work
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

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
                        is = Quests.get().getItemStack("", (org.bukkit.configuration.ConfigurationSection) configBlock);
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
