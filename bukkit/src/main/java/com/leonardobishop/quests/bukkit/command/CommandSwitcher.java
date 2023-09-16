package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.util.Messages;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandSwitcher implements CommandHandler {

    protected final Map<String, CommandHandler> subcommands = new HashMap<>();
    protected final Map<String, String> aliases = new HashMap<>();
    private final int switchingIndex;

    public CommandSwitcher(int switchingIndex) {
        this.switchingIndex = switchingIndex;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > switchingIndex) {
            String subcommand = args[switchingIndex].toLowerCase();
            CommandHandler handler = subcommands.getOrDefault(subcommand, subcommands.get(aliases.get(subcommand)));
            if (handler != null) {
                if ((handler.getPermission() == null || sender.hasPermission(handler.getPermission()))) {
                    handler.handle(sender, args);
                } else {
                    Messages.COMMAND_NO_PERMISSION.send(sender);
                }
                return;
            }
        }
        showHelp(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > switchingIndex + 1) {
            String subcommand = args[switchingIndex].toLowerCase();
            CommandHandler handler = subcommands.getOrDefault(subcommand, subcommands.get(aliases.get(subcommand)));
            if (handler != null && (handler.getPermission() == null || sender.hasPermission(handler.getPermission()))) {
                return handler.tabComplete(sender, args);
            }
        } else if (args.length == switchingIndex + 1) {
            List<String> availableCommands = new ArrayList<>();
            for (Map.Entry<String, CommandHandler> command : subcommands.entrySet()) {
                String permission = command.getValue().getPermission();
                if (permission == null || sender.hasPermission(permission)) availableCommands.add(command.getKey());
            }
            return TabHelper.matchTabComplete(args[switchingIndex], availableCommands);
        }
        return Collections.emptyList();
    }

    abstract public void showHelp(CommandSender sender);

}
