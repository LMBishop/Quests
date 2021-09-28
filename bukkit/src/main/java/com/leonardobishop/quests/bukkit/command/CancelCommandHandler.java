package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CancelCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public CancelCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
            return;
        }

        Quest quest;
        if (qPlayer.getQuestProgressFile().getStartedQuests().size() == 1) {
            quest = qPlayer.getQuestProgressFile().getStartedQuests().get(0);
        } else if (args.length >= 2) {
            quest = plugin.getQuestManager().getQuestById(args[1]);
            if (quest == null) {
                sender.sendMessage(Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
                return;
            }
        } else {
            sender.sendMessage(Messages.COMMAND_QUEST_CANCEL_SPECIFY.getMessage());
            return;
        }
        qPlayer.cancelQuest(quest);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
