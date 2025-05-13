package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AdminModdataRandomCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminModdataRandomCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length >= 4) {
            CommandUtils.useOtherPlayer(sender, args[3], plugin, (qPlayer) -> {
                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

                List<Quest> validQuests = new ArrayList<>();
                boolean fromCategory = args.length != 4;
                if (!fromCategory) {
                    for (Quest quest : plugin.getQuestManager().getQuestMap().values()) {
                        if (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                            validQuests.add(quest);
                        }
                    }
                } else {
                    Category category = plugin.getQuestManager().getCategoryById(args[4]);
                    if (category == null) {
                        Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.send(sender, "{category}", args[1]);
                    } else {
                        for (String questId : category.getRegisteredQuestIds()) {
                            Quest quest = plugin.getQuestManager().getQuestById(questId);
                            if (quest == null) continue;
                            if (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                                validQuests.add(quest);
                            }
                        }
                    }
                }

                if (validQuests.isEmpty()) {
                    if (fromCategory) {
                        Messages.COMMAND_QUEST_ADMIN_RANDOM_CATEGORY_NONE.send(sender,
                                "{player}", args[3],
                                "{category}", args[4]);
                    } else {
                        Messages.COMMAND_QUEST_ADMIN_RANDOM_NONE.send(sender, "{player}", args[3]);
                    }
                    return;
                }
                int random = ThreadLocalRandom.current().nextInt(0, validQuests.size());
                Quest quest = validQuests.get(random);
                qPlayer.startQuest(quest);

                if (fromCategory) {
                    Messages.COMMAND_QUEST_ADMIN_RANDOM_CATEGORY_SUCCESS.send(sender,
                            "{player}", args[3],
                            "{category}", args[4],
                            "{quest}", quest.getId());
                } else {
                    Messages.COMMAND_QUEST_ADMIN_RANDOM_SUCCESS.send(sender,
                            "{player}", args[3],
                            "{quest}", quest.getId());
                }

                CommandUtils.doSafeSave(this.plugin, qPlayer);
            });
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin moddata random <player> [category]");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        } else if (args.length == 5) {
            return TabHelper.tabCompleteCategory(args[4]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
