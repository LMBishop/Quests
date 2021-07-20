package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminModdataResetCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminModdataResetCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 4) {
            QPlayer qPlayer = CommandUtils.getOtherPlayer(sender, args[3], plugin);
            if (qPlayer == null) return;
            Quest quest = plugin.getQuestManager().getQuestById(args[4]);
            if (quest == null) {
                sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[4]));
                return;
            }
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            questProgressFile.generateBlankQuestProgress(quest);
            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_RESET_SUCCESS.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));

            if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
            }
            return;
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin moddata start <player> <quest>");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
