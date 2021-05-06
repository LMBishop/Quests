package com.leonardobishop.quests.api;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Quest;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestsPlaceholders extends PlaceholderExpansion {
    private final Quests plugin;

    public QuestsPlaceholders(Quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "quests";
    }

    @Override
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null)
            return "";
        QPlayer questPlayer = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (identifier.equals("current_quest_amount")) {
            return String.valueOf(questPlayer.getQuestProgressFile().getStartedQuests().size());
        }
        if (identifier.equals("current_quest_names")) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            List<String> list = new ArrayList<>();
            for (Quest currentQuests : questPlayer.getQuestProgressFile().getStartedQuests()) {
                list.add(currentQuests.getDisplayNameStripped());
            }
            Collections.sort(list);
            for (String questName : list) {
                if (!first) {
                    sb.append("\n");
                }
                first = false;
                sb.append(questName);
            }
            return sb.toString();
        }
        if (identifier.startsWith("has_current_quest_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            for (Quest currentQuests : questPlayer.getQuestProgressFile().getStartedQuests()) {
                if (currentQuests.getId().equals(questId)) {
                    return "true";
                }
            }
            return "false";
        }
        if (identifier.startsWith("has_completed_quest_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            Quest quest = this.plugin.getQuestManager().getQuestById(questId);
            if (quest != null) {
                if (questPlayer.getQuestProgressFile().getQuestProgress(quest).isCompleted())
                    return "true";
            }
            return "false";
        }
        if (identifier.startsWith("has_completed_before_quest_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            Quest quest = this.plugin.getQuestManager().getQuestById(questId);
            if (quest != null) {
                if (questPlayer.getQuestProgressFile().getQuestProgress(quest).isCompletedBefore()) {
                    return "true";
                }
            }
            return "false";
        }
        if (identifier.startsWith("cooldown_time_remaining_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            Quest quest = this.plugin.getQuestManager().getQuestById(questId);
            if (quest != null) {
                if (questPlayer.getQuestProgressFile().getQuestProgress(quest).isCompleted()) {
                    return this.plugin.convertToFormat(TimeUnit.SECONDS.convert(questPlayer.getQuestProgressFile().getCooldownFor(quest), TimeUnit.MILLISECONDS));
                }
            }
            return this.plugin.convertToFormat(0);
        }
        if (identifier.startsWith("can_accept_quest_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            Quest quest = this.plugin.getQuestManager().getQuestById(questId);
            if (quest != null) {
                if (questPlayer.getQuestProgressFile().canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                    return "true";
                }
            }
            return "false";
        }
        if (identifier.startsWith("meets_requirements_")) {
            String questId = identifier.substring(identifier.lastIndexOf("_") + 1);
            Quest quest = this.plugin.getQuestManager().getQuestById(questId);
            if (quest != null) {
                if (questPlayer.getQuestProgressFile().hasMetRequirements(quest)) {
                    return "true";
                }
            }
            return "false";
        }
        return "";
    }
}
