package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminDebugQuestCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminDebugQuestCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Your data file is not loaded.");
                return;
            }

            String questId = args[3];
            if (!questId.equals("*")) {
                Quest quest = plugin.getQuestManager().getQuestById(questId);
                if (quest == null) {
                    sender.sendMessage(ChatColor.RED + "Quest " + questId + " does not exist.");
                    return;
                }
            }


            QPlayerPreferences preferences = qPlayer.getPlayerPreferences();
            QPlayerPreferences.DebugType currentDebugType = preferences.getDebug(questId);
            String questName = questId.equals("*") ? "all quests" : "quest " + questId;
            if (currentDebugType == null) {
                if (args.length < 5) {
                    sender.sendMessage(ChatColor.RED + "You must specify a debug type.");
                    return;
                }

                String debugType = args[4];
                QPlayerPreferences.DebugType debugTypeEnum;

                try {
                    debugTypeEnum = QPlayerPreferences.DebugType.valueOf(debugType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid debug type.");
                    return;
                }

                preferences.setDebug(questId, debugTypeEnum);
                sender.sendMessage(ChatColor.GREEN + "Debugging enabled for " + questName + ".");
                sender.sendMessage(ChatColor.GRAY + "You will now see debug logs for "
                        + questName
                        + " for "
                        + (debugTypeEnum == QPlayerPreferences.DebugType.SELF ? "yourself" : "everybody on the server") +
                        ". This may generate a lot of spam.");
                sender.sendMessage(ChatColor.GRAY + "Use '/quests admin debug " + questId + "' to disable.");
            } else {
                preferences.unsetDebug(questId);
                sender.sendMessage(ChatColor.GREEN + "Debugging disabled for " + questName + ".");
            }

            // Set it here to optimize debugging on high player count servers
            final Set<QPlayer> debuggers = new HashSet<>();
            for (final QPlayer debugger : this.plugin.getPlayerManager().getQPlayers()) {
                if (debugger.getPlayerPreferences().isDebug()) {
                    debuggers.add(debugger);
                }
            }
            QPlayerPreferences.setDebuggers(debuggers);
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return TabHelper.tabCompleteQuestsOrWildcard(args[3]);
        } else if (args.length == 5) {
            return TabHelper.matchTabComplete(args[4], Arrays.asList("self", "all"));
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
