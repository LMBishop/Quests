package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public RandomCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            Messages.COMMAND_DATA_NOT_LOADED.send(player);
            return;
        }
        List<Quest> validQuests = new ArrayList<>();
        if (args.length == 1) {
            for (Quest quest : plugin.getQuestManager().getQuestMap().values()) {
                if (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                    validQuests.add(quest);
                }
            }
        } else {
            Category category = plugin.getQuestManager().getCategoryById(args[1]);
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
            Messages.QUEST_RANDOM_NONE.send(player);
            return;
        }
        int random = ThreadLocalRandom.current().nextInt(0, validQuests.size());
        qPlayer.startQuest(validQuests.get(random));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.command.random";
    }
}
