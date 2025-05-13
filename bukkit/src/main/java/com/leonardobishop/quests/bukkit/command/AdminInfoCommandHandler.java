package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminInfoCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminInfoCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.GRAY + "Loaded quests:");
            int i = 0;
            for (Quest quest : plugin.getQuestManager().getQuestMap().values()) {
                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + quest.getId() + ChatColor.GRAY + " [" + quest.getTasks().size() + " tasks]");
                i++;
                if (i == 25 && plugin.getQuestManager().getQuestMap().size() > 25) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " ... and " + (plugin.getQuestManager().getQuestMap().size() - 25) + " more ...");
                    break;
                }
            }
            sender.sendMessage(ChatColor.GRAY + "Quest controller: " + ChatColor.RED + plugin.getQuestController().getName());
            sender.sendMessage(ChatColor.GRAY.toString() + plugin.getQuestManager().getQuestMap().size() + " registered.");
            sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a info [quest].");
        } else {
            Quest quest = plugin.getQuestManager().getQuestById(args[2]);
            if (quest == null) {
                Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.send(sender, "{quest}", args[2]);
            } else {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Information for quest '" + quest.getId() + "'");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE + "Task configurations (" + quest.getTasks().size() + ")");
                for (Task task : quest.getTasks()) {
                    sender.sendMessage(ChatColor.RED + "Task '" + task.getId() + "':");
                    for (Map.Entry<String, Object> config : task.getConfigValues().entrySet()) {
                        sender.sendMessage(ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + config.getKey() + ": " + ChatColor.GRAY + ChatColor.ITALIC + config.getValue());
                    }
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Start string");
                for (String s : quest.getStartString()) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Reward string");
                for (String s : quest.getRewardString()) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Rewards");
                for (String s : quest.getRewards()) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                }
                sender.sendMessage(ChatColor.RED + "Vault reward: " + ChatColor.GRAY + quest.getVaultReward());
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Quest options");
                sender.sendMessage(ChatColor.RED + "Category: " + ChatColor.GRAY + quest.getCategoryId());
                sender.sendMessage(ChatColor.RED + "Repeatable: " + ChatColor.GRAY + quest.isRepeatable());
                sender.sendMessage(ChatColor.RED + "Requirements: " + ChatColor.GRAY + String.join(", ", quest.getRequirements()));
                sender.sendMessage(ChatColor.RED + "Cooldown enabled: " + ChatColor.GRAY + quest.isCooldownEnabled());
                sender.sendMessage(ChatColor.RED + "Cooldown time: " + ChatColor.GRAY + quest.getCooldown());
                sender.sendMessage(ChatColor.RED + "Autostart: " + ChatColor.GRAY + quest.isAutoStartEnabled());
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return TabHelper.tabCompleteQuests(args[2]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
