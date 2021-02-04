package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CitizensDeliverTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public CitizensDeliverTaskType() {
        super("citizens_deliver", "LMBishop", "Deliver a set of items to a NPC.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of item to retrieve."));
        this.creatorConfigValues.add(new ConfigValue("item", true, "Name or ID of item."));
        this.creatorConfigValues.add(new ConfigValue("npc-name", true, "Name of the NPC."));
        this.creatorConfigValues.add(new ConfigValue("remove-items-when-complete", false, "Take the items away from the player on completion (true/false, " +
                "default = false)."));
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
        TaskUtils.configValidateExists(root + ".npc-name", config.get("npc-name"), problems, "npc-name", super.getType());
        TaskUtils.configValidateBoolean(root + ".remove-items-when-complete", config.get("remove-items-when-complete"), problems, true, "remove-items-when-complete", super.getType());
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCClick(NPCRightClickEvent event) {
        Bukkit.getScheduler().runTaskLater(Quests.get(), () -> checkInventory(event.getClicker(), event.getNPC().getName()), 1L);
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player, String citizenName) {
        if (player == null || !player.isOnline()) {
            return;
        }
        QPlayer qPlayer = Quests.get().getPlayerManager().getPlayer(player.getUniqueId(), true);
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.valueOf(task.getConfigValue("npc-name")))).equals(ChatColor
                            .stripColor(ChatColor.translateAlternateColorCodes('&', citizenName)))) {
                        return;
                    }
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
                        is = Quests.get().getItemStack(null, (org.bukkit.configuration.ConfigurationSection) configBlock);
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
