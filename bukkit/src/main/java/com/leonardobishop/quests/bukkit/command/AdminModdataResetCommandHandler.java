package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.QuestStartResult;
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
            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
            Quest quest = plugin.getQuestManager().getQuestById(args[4]);
            if (quest == null) {
                sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[4]));
                return;
            }
            QuestStartResult response = qPlayer.startQuest(quest);
            switch (response) {
                case QUEST_LIMIT_REACHED:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLIMIT.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case QUEST_ALREADY_COMPLETED:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOMPLETE.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case QUEST_COOLDOWN:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case QUEST_LOCKED:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLOCKED.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case QUEST_ALREADY_STARTED:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILSTARTED.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case QUEST_NO_PERMISSION:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILPERMISSION.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
                case NO_PERMISSION_FOR_CATEGORY:
                    sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                    return;
            }

            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));

            if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
            }
            return;
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin moddata reset <player> <quest>");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
