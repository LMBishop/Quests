package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

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

        super.aliases.put("q", "quest");
        super.aliases.put("c", "category");
        super.aliases.put("a", "admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getTaskTypeManager().areRegistrationsAccepted()) {
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
                player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                return true;
            }
            plugin.getMenuController().openMainMenu(qPlayer);
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
        Set<String> permissions = plugin.getConfig().getConfigurationSection("command.player-help").getKeys(false);
        for (String permission : permissions) {
            if(!permission.contains("default") && !sender.hasPermission(subcommands.get(permission).getPermission())) continue;
            for(String message : plugin.getConfig().getStringList("command.player-help."+permission)) {
                if(message.contains("%version%")) {
                    message = message.replace("%version%", plugin.getDescription().getVersion());
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
