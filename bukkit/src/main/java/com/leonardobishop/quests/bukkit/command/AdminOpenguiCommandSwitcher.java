package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminOpenguiCommandSwitcher extends CommandSwitcher {

    private final BukkitQuestsPlugin plugin;

    public AdminOpenguiCommandSwitcher(BukkitQuestsPlugin plugin) {
        super(2);
        this.plugin = plugin;

        super.subcommands.put("quest", new AdminOpenguiQuestCommandHandler(plugin));
        super.subcommands.put("category", new AdminOpenguiCategoryCommandHandler(plugin));
        super.subcommands.put("started", new AdminOpenguiStartedCommandHandler(plugin));

        super.aliases.put("q", "quest");
        super.aliases.put("quests", "quest");
        super.aliases.put("c", "category");
        super.aliases.put("categories", "category");
        super.aliases.put("s", "started");
    }

    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: opengui " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui q/quest <player> " + ChatColor.DARK_GRAY + ": forcefully show" +
                " quests for player");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui s/started <player> " + ChatColor.DARK_GRAY + ": forcefully show" +
                " started quests for player");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui c/category <player> <category> " + ChatColor.DARK_GRAY + ": " +
                "forcefully " +
                "open category by ID for player");
        sender.sendMessage(ChatColor.GRAY + "These commands are useful for command NPCs. These will bypass the usual quests.command permission.");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
