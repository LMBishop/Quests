package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class QuestCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public QuestCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length >= 3) {
            Quest quest = plugin.getQuestManager().getQuestById(args[1]);
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                return;
            }
            if (quest == null) {
                sender.sendMessage(Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
            }
            if (args[2].equalsIgnoreCase("s") || args[2].equalsIgnoreCase("start")) {
                qPlayer.startQuest(quest);
            } else if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("cancel")) {
                qPlayer.cancelQuest(quest);
            } else if (args[2].equalsIgnoreCase("t") || args[2].equalsIgnoreCase("track")) {
                qPlayer.trackQuest(quest);
            } else {
                sender.sendMessage(Messages.COMMAND_SUB_DOESNTEXIST.getMessage().replace("{sub}", args[2]));
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + "/quests q/quest <categoryid> (start|cancel|track)");
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }

}
