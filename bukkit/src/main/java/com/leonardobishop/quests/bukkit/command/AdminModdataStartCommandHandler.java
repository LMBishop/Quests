package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminModdataStartCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminModdataStartCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 4) {
            Quest quest = plugin.getQuestManager().getQuestById(args[4]);
            if (quest == null) {
                Messages.COMMAND_QUEST_START_DOESNTEXIST.send(sender, "{quest}", args[4]);
                return;
            }

            CommandUtils.useOtherPlayer(sender, args[3], plugin, qPlayer -> {
                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                QuestStartResult response = qPlayer.startQuest(quest);
                switch (response) {
                    case QUEST_LIMIT_REACHED:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILLIMIT.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case QUEST_ALREADY_COMPLETED:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILCOMPLETE.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case QUEST_COOLDOWN:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case QUEST_LOCKED:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILLOCKED.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case QUEST_ALREADY_STARTED:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILSTARTED.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case QUEST_NO_PERMISSION:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILPERMISSION.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                    case NO_PERMISSION_FOR_CATEGORY:
                        Messages.COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION.send(sender, "{player}", args[3], "{quest}", quest.getId());
                        return;
                }

                Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.send(sender, "{player}", args[3], "{quest}", quest.getId());

                CommandUtils.doSafeSave(this.plugin, qPlayer);
            });
            return;
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin moddata start <player> <quest>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        } else if (args.length == 5) {
            return TabHelper.tabCompleteQuests(args[4]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
