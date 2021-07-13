package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
            player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
            return;
        }
        List<Quest> validQuests = new ArrayList<>();
        for (Quest quest : plugin.getQuestManager().getQuests().values()) {
            if (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                validQuests.add(quest);
            }
        }

        if (validQuests.isEmpty()) {
            player.sendMessage(Messages.QUEST_RANDOM_NONE.getMessage());
            return;
        }
        int random = ThreadLocalRandom.current().nextInt(0, validQuests.size());
        qPlayer.startQuest(validQuests.get(random));
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.command.random";
    }
}
