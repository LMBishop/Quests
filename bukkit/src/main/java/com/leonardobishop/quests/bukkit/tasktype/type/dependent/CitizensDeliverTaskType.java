package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CitizensDeliverTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CitizensDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a NPC.");
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
        TaskUtils.configValidateExists(root + ".npc-name", config.get("npc-name"), problems, "npc-name", super.getType());
        TaskUtils.configValidateBoolean(root + ".remove-items-when-complete", config.get("remove-items-when-complete"), problems, true, "remove-items-when-complete", super.getType());
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCClick(NPCRightClickEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkInventory(event.getClicker(), event.getNPC().getName()), 1L);
    }

    @SuppressWarnings("deprecation")
    private void checkInventory(Player player, String citizenName) {
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
                    if (!Chat.strip(Chat.color(String.valueOf(task.getConfigValue("npc-name"))))
                            .equals(Chat.strip(Chat.color(citizenName)))) {
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
