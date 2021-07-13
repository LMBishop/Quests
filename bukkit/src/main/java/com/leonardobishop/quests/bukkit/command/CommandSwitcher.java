package com.leonardobishop.quests.bukkit.command;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
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
            if (handler != null && (handler.getPermission() == null || sender.hasPermission(handler.getPermission()))) {
                handler.handle(sender, args);
                return;
            }
        }
        showHelp(sender);
    }

    abstract public void showHelp(CommandSender sender);

}
