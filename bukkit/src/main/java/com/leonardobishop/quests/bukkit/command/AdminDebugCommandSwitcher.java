package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminDebugCommandSwitcher extends CommandSwitcher {

    public AdminDebugCommandSwitcher(BukkitQuestsPlugin plugin) {
        super(2);

        super.subcommands.put("player", new AdminDebugPlayerCommandHandler(plugin));
        super.subcommands.put("quest", new AdminDebugQuestCommandHandler(plugin));
        super.subcommands.put("report", new AdminDebugReportCommandHandler(plugin));
    }

    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: debug " + ChatColor
                .GRAY + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a debug player <player> " + ChatColor.DARK_GRAY
                + ": show quests progression data of a player");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a debug report " + ChatColor.DARK_GRAY
                + ": generate a debug report");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a debug quest <player> <self|all> " + ChatColor.DARK_GRAY
                + ": enable debug logging for a specific quest");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
