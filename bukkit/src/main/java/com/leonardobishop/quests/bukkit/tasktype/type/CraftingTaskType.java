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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CraftingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CraftingTaskType(BukkitQuestsPlugin plugin) {
        super("crafting", TaskUtils.TASK_ATTRIBUTION_STRING, "Craft a specific item.");
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
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {
        if (event.getClickedInventory() == null
                || (event.getClickedInventory().getType() != InventoryType.CRAFTING && event.getClickedInventory().getType() != InventoryType.WORKBENCH)
                || event.getSlotType() != InventoryType.SlotType.RESULT
                || event.getCurrentItem() == null) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        int clickedAmount = getCraftAmount(event);

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

                    if (!clickedItem.isSimilar(is)) continue;

                    int progress;
                    if (taskProgress.getProgress() == null) {
                        progress = 0;
                    } else {
                        progress = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progress + clickedAmount);

                    if ((int) taskProgress.getProgress() >= amount) {
                        taskProgress.setProgress(amount);
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

    // thanks https://www.spigotmc.org/threads/util-get-the-crafted-item-amount-from-a-craftitemevent.162952/
    private int getCraftAmount(CraftItemEvent e) {
        if(e.isCancelled()) { return 0; }

        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : e.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = e.getRecipe().getResult().getAmount() * possibleCreations;

            ItemStack i = e.getRecipe().getResult();

            for(int s = 0; s <= 35; s++) {
                ItemStack test = p.getInventory().getItem(s);
                if(test == null || test.getType() == Material.AIR) {
                    amountCanBeMade+= i.getMaxStackSize();
                    continue;
                }
                if(test.isSimilar(i)) {
                    amountCanBeMade += i.getMaxStackSize() - test.getAmount();
                }
            }

            return amountOfItems > amountCanBeMade ? amountCanBeMade : amountOfItems;
        } else {
            return e.getRecipe().getResult().getAmount();
        }
    }

}
