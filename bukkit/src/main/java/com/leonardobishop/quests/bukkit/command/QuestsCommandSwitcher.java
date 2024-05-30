package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuestsCommandSwitcher extends CommandSwitcher implements TabExecutor {

    private final BukkitQuestsPlugin plugin;

    public QuestsCommandSwitcher(BukkitQuestsPlugin plugin) {
        super(0);
        this.plugin = plugin;

        super.subcommands.put("quest", new QuestCommandHandler(plugin));
        super.subcommands.put("category", new CategoryCommandHandler(plugin));
        super.subcommands.put("random", new RandomCommandHandler(plugin));
        super.subcommands.put("started", new StartedCommandHandler(plugin));
        super.subcommands.put("admin", new AdminCommandSwitcher(plugin));
        super.subcommands.put("start", new StartCommandHandler(plugin));
        super.subcommands.put("track", new TrackCommandHandler(plugin));
        super.subcommands.put("cancel", new CancelCommandHandler(plugin));

        super.aliases.put("q", "quest");
        super.aliases.put("c", "category");
        super.aliases.put("a", "admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getTaskTypeManager().areRegistrationsOpen()) {
            sender.sendMessage(ChatColor.RED + "Quests is not ready yet.");
            return true;
        }
        if (!plugin.isValidConfiguration()
                && !(args.length >= 2 && (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin"))
                && args[1].equalsIgnoreCase("reload"))) {
            sender.sendMessage(ChatColor.RED + "Quests cannot be used right now. Please speak to an administrator.");
            if (sender.hasPermission("quests.admin")) {
                CommandUtils.showProblems(sender, plugin.getConfigProblems());
                sender.sendMessage(ChatColor.RED + "The main config (config.yml) must be in tact before quests can be used. " +
                        "Please use the above information to help rectify the problem.");
            }
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                Messages.COMMAND_DATA_NOT_LOADED.send(player);
                return true;
            }
            MenuUtils.openMainMenu(plugin, qPlayer);
            return true;
        }

        super.handle(sender, args);
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return super.tabComplete(sender, args);
    }

    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests v" + plugin
                .getDescription().getVersion() + " " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests " + ChatColor.DARK_GRAY + ": show quests");
        if (sender.hasPermission(subcommands.get("category").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests c/category <categoryid> " + ChatColor.DARK_GRAY + ": open category by ID");
        }
        if (sender.hasPermission(subcommands.get("started").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests started " + ChatColor.DARK_GRAY + ": show started quests");
        }
        if (sender.hasPermission(subcommands.get("quest").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests q/quest <questid> (start|cancel|track) " + ChatColor.DARK_GRAY + ": start, cancel or track quest by ID");
        }
        if (sender.hasPermission(subcommands.get("start").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests start <questid>" + ChatColor.DARK_GRAY + ": start quest by name");
        }
        if (sender.hasPermission(subcommands.get("track").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests track <questid>" + ChatColor.DARK_GRAY + ": track quest by name");
        }
        if (sender.hasPermission(subcommands.get("cancel").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests cancel [questid]" + ChatColor.DARK_GRAY + ": cancel active quest by name");
        }
        if (sender.hasPermission(subcommands.get("random").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests random " + ChatColor.DARK_GRAY + ": show random quests");
        }
        if (sender.hasPermission(subcommands.get("admin").getPermission())) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a/admin " + ChatColor.DARK_GRAY + ": view help for admins");
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------=[" + ChatColor.RED + " made with <3 by LMBishop " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=--------");
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
