package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminModdataFullresetCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminModdataFullresetCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 3) {
            QPlayer qPlayer = CommandUtils.getOtherPlayer(sender, args[3], plugin);
            if (qPlayer == null) return;
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            questProgressFile.clear();
            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
            if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
            }
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_FULLRESET.getMessage().replace("{player}", args[3]));

            if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
            }
            return;
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin moddata fullreset <player>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
