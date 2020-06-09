package com.leonardobishop.quests.api;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null || !p.isOnline())
            return null;

        String[] key = params.split("_", 5);
        QPlayer questP = this.plugin.getPlayerManager().getPlayer(p.getUniqueId());

        if (key[0].equals("all") || key[0].equals("completed") || key[0].equals("completedBefore") || key[0].equals("started") || key[0].equals("categories")) {
            if (key.length == 1) {
                switch (key[0]) {
                    case "all":
                        return String.valueOf(this.plugin.getQuestManager().getQuests().size());
                    case "completed":
                        return String.valueOf(questP.getQuestProgressFile().getQuestsProgress("completed").size());
                    case "completedBefore":
                        return String.valueOf(questP.getQuestProgressFile().getQuestsProgress("completedBefore").size());
                    case "started":
                        return String.valueOf(questP.getQuestProgressFile().getQuestsProgress("started").size());
                    case "categories":
                        return String.valueOf(this.plugin.getQuestManager().getCategories().size());
                }
            }
            if (key[1].equals("list") || key[1].equals("l")) {
                String separator = ",";
                if (!(key.length == 2)) {
                    separator = key[2];
                }

                switch (key[0]) {
                    case "all":
                        return String.join(separator, this.plugin.getQuestManager().getQuests().toString());
                    case "categories":
                        return String.join(separator, this.plugin.getQuestManager().getCategories().toString());
                    case "completed":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : questP.getQuestProgressFile().getQuestsProgress("completed")) {
                            listCompleted.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompleted);
                    case "completedBefore":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : questP.getQuestProgressFile().getQuestsProgress("completedBefore")) {
                            listCompletedBefore.add(qCompletedBefore.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : questP.getQuestProgressFile().getQuestsProgress("started")) {
                            listStarted.add(qStarted.getDisplayNameStripped());
                        }
                        return String.join(separator, listStarted);
                }
            }
            return "null";
        }

        if (key[0].startsWith("quest:") || key[0].startsWith("q:")) {
            Quest questId = this.plugin.getQuestManager().getQuestById(key[0].substring(key[0].lastIndexOf(":") + 1));

            if (key[1].equals("started") || key[1].equals("s")) {
                if (questId != null && questP.getQuestProgressFile().getQuestProgress(questId).isStarted()) {
                    return "true";
                }
                return "false";
            }
            if (key[1].equals("completed") || key[1].equals("c")) {
                if (questId != null && questP.getQuestProgressFile().getQuestProgress(questId).isCompleted()) {
                    return "true";
                }
                return "false";
            }
            if (key[1].equals("completedBefore") || key[1].equals("cB")) {
                if (questId != null && questP.getQuestProgressFile().getQuestProgress(questId).isCompletedBefore()) {
                    return "true";
                }
                return "false";
            }
            if (key[1].equals("completionDate")) {
                if (questId != null && questP.getQuestProgressFile().getQuestProgress(questId).isCompleted()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(questP.getQuestProgressFile().getQuestProgress(questId).getCompletionDate());
                }
                return "Never";
            }
            if (key[1].equals("cooldown")) {
                if (questId != null && questP.getQuestProgressFile().getQuestProgress(questId).isCompleted()) {
                    String time = this.plugin.convertToFormat(TimeUnit.SECONDS.convert(questP.getQuestProgressFile().getCooldownFor(questId), TimeUnit.MILLISECONDS));
                    if (time.startsWith("-")) {
                        return "0";
                    }
                    return time;
                }
                return "0";
            }
            if (key[1].equals("canAccept")) {
                if (questId != null && questP.getQuestProgressFile().canStartQuest(questId) == QuestStartResult.QUEST_SUCCESS) {
                    return "true";
                }
                return "false";
            }
            if (key[1].equals("meetsRequirements")) {
                if (questId != null && questP.getQuestProgressFile().hasMetRequirements(questId)) {
                    return "true";
                }
                return "false";
            }
            if (key[1].startsWith("task") || key[1].startsWith("t")) {
                String[] t = key[1].split(":");
                if (key[2].equals("progress") || key[2].equals("p")) {
                    if (questId == null || questP.getQuestProgressFile().getQuestProgress(questId).getTaskProgress(t[1]).getProgress() == null) {
                        return "0";
                    }
                    return String.valueOf(questP.getQuestProgressFile().getQuestProgress(questId).getTaskProgress(t[1]).getProgress());
                }
                if (key[2].equals("completed") || key[2].equals("c")) {
                    if (questId == null || questP.getQuestProgressFile().getQuestProgress(questId).getTaskProgress(t[1]).isCompleted()) {
                        return "true";
                    }
                    return "false";
                }
            }
            return "null";
        }

        if (key[0].startsWith("category:") || key[0].startsWith("c:")) {
            if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
                return "Categories Disabled";
            }
            Category categoryId = this.plugin.getQuestManager().getCategoryById(key[0].substring(key[0].lastIndexOf(":") + 1));
            if (key.length == 2) {
                switch (key[1]) {
                    case "all":
                    case "a":
                        return String.valueOf(categoryId.getRegisteredQuestIds().size());
                    case "completed":
                    case "c":
                        return String.valueOf(getCategoryQuests(questP, categoryId, "completed").size());
                    case "completedBefore":
                    case "cB":
                        return String.valueOf(getCategoryQuests(questP, categoryId, "completedBefore").size());
                    case "started":
                    case "s":
                        return String.valueOf(getCategoryQuests(questP, categoryId, "started").size());
                }
            }
            if (key[2].equals("list") || key[2].equals("l")) {
                String separator = ",";
                if (!(key.length == 3)) {
                    separator = key[3];
                }

                switch (key[1]) {
                    case "all":
                    case "a":
                        List<String> listAll = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, categoryId, "all")) {
                            listAll.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listAll);
                    case "completed":
                    case "c":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, categoryId, "completed")) {
                            listCompleted.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompleted);
                    case "completedBefore":
                    case "cB":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : getCategoryQuests(questP, categoryId, "completedBefore")) {
                            listCompletedBefore.add(qCompletedBefore.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                    case "s":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : getCategoryQuests(questP, categoryId, "started")) {
                            listStarted.add(qStarted.getDisplayNameStripped());
                        }
                        return String.join(separator, listStarted);
                }
            }
            return "null";
        }
        return null;
    }

    public List<Quest> getCategoryQuests(QPlayer questP, Category category, String type) {
        List<Quest> CategoryQuests = new ArrayList<>();
        if (type.equals("all")) {
            for (String cQuests : category.getRegisteredQuestIds()) {
                CategoryQuests.add(plugin.getQuestManager().getQuestById(cQuests));
            }
        }
        if (type.equals("completed")) {
            for (String cQuests : category.getRegisteredQuestIds()) {
                Quest quest = plugin.getQuestManager().getQuestById(cQuests);
                if (questP.getQuestProgressFile().getQuestProgress(quest).isCompleted()) {
                    CategoryQuests.add(quest);
                }
            }
        }
        if (type.equals("completedBefore")) {
            for (String cQuests : category.getRegisteredQuestIds()) {
                Quest quest = plugin.getQuestManager().getQuestById(cQuests);
                if (questP.getQuestProgressFile().getQuestProgress(quest).isCompletedBefore()) {
                    CategoryQuests.add(quest);
                }
            }
        }
        if (type.equals("started")) {
            for (String cQuests : category.getRegisteredQuestIds()) {
                Quest quest = plugin.getQuestManager().getQuestById(cQuests);
                if (questP.getQuestProgressFile().getQuestProgress(quest).isStarted()) {
                    CategoryQuests.add(quest);
                }
            }
        }
        return CategoryQuests;
    }
}
