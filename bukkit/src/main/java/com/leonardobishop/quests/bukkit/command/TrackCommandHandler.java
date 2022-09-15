package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TrackCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public TrackCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length >= 2) {
            Quest quest = plugin.getQuestManager().getQuestById(args[1]);
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                Messages.COMMAND_DATA_NOT_LOADED.send(player);
                return;
            }
            if (quest == null) {
                Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.send(sender, "{quest}", args[1]);
                return;
            } else {
                qPlayer.trackQuest(quest);
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + "/quests track <questid>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabHelper.tabCompleteQuests(args[1]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.command.track";
    }

}
