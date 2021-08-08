package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminModdataCommandSwitcher extends CommandSwitcher {

    private final BukkitQuestsPlugin plugin;

    public AdminModdataCommandSwitcher(BukkitQuestsPlugin plugin) {
        super(2);
        this.plugin = plugin;

        super.subcommands.put("fullreset", new AdminModdataFullresetCommandHandler(plugin));
        super.subcommands.put("start", new AdminModdataStartCommandHandler(plugin));
        super.subcommands.put("reset", new AdminModdataResetCommandHandler(plugin));
        super.subcommands.put("complete", new AdminModdataCompleteCommandHandler(plugin));
        super.subcommands.put("random", new AdminModdataRandomCommandHandler(plugin));
    }

    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: moddata " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata fullreset <player> " + ChatColor.DARK_GRAY + ": clear a " +
                "players quest data file");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata reset <player> <questid> " + ChatColor.DARK_GRAY + ": clear a " +
                "players data for specifc quest");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata start <player> <questid> " + ChatColor.DARK_GRAY + ": start a " +
                "quest for a player");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata complete <player> <questid> " + ChatColor.DARK_GRAY + ": " +
                "complete a quest for a player");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata random <player> [category] " + ChatColor.DARK_GRAY + ": " +
                "start a random quest for a player [in a specific category]");
        sender.sendMessage(ChatColor.GRAY + "These commands modify quest progress for players. Use them cautiously. Changes are irreversible.");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
