package com.leonardobishop.quests.api;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
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

    //TODO maybe cache these results for a bit? all these calls could be heavy

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null || !p.isOnline())
            return null;

        String[] key = params.split("_", 5);
        QPlayer questP = this.plugin.getPlayerManager().getPlayer(p.getUniqueId());

        if (key[0].equals("all") || key[0].equals("completed") || key[0].equals("completedBefore")
                || key[0].equals("completedbefore") || key[0].equals("started") || key[0].equals("categories")) {
            if (key.length == 1) {
                switch (key[0]) {
                    case "all":
                        return String.valueOf(this.plugin.getQuestManager().getQuests().size());
                    case "completed":
                        return String.valueOf(questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED).size());
                    case "completedbefore":
                    case "completedBefore":
                        return String.valueOf(questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE).size());
                    case "started":
                        return String.valueOf(questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.STARTED).size());
                    case "categories":
                        return String.valueOf(this.plugin.getQuestManager().getCategories().size());
                }
            } else if (key[1].equals("list") || key[1].equals("l")) {
                String separator = ",";
                if (!(key.length == 2)) {
                    separator = key[2];
                }

                switch (key[0]) {
                    case "all":
                        List<String> listAll = new ArrayList<>();
                        for (Quest q : this.plugin.getQuestManager().getQuests().values()) {
                            listAll.add(q.getDisplayNameStripped());
                        }
                        return String.join(separator, listAll);
                    case "categories":
                        List<String> listCategories = new ArrayList<>();
                        for (Category c : this.plugin.getQuestManager().getCategories()) {
                            listCategories.add(c.getDisplayNameStripped());
                        }
                        return String.join(separator, listCategories);
                    case "completed":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED)) {
                            listCompleted.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompleted);
                    case "completedbefore":
                    case "completedBefore":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE)) {
                            listCompletedBefore.add(qCompletedBefore.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.STARTED)) {
                            listStarted.add(qStarted.getDisplayNameStripped());
                        }
                        return String.join(separator, listStarted);
                }
            } else if (key[1].equals("listid") || key[1].equals("lid")) {
                String separator = ",";
                if (!(key.length == 2)) {
                    separator = key[2];
                }

                switch (key[0]) {
                    case "all":
                        List<String> listAll = new ArrayList<>();
                        for (Quest q : this.plugin.getQuestManager().getQuests().values()) {
                            listAll.add(q.getId());
                        }
                        return String.join(separator, listAll);
                    case "categories":
                        List<String> listCategories = new ArrayList<>();
                        for (Category c : this.plugin.getQuestManager().getCategories()) {
                            listCategories.add(c.getId());
                        }
                        return String.join(separator, listCategories);
                    case "completed":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED)) {
                            listCompleted.add(qCompleted.getId());
                        }
                        return String.join(separator, listCompleted);
                    case "completedbefore":
                    case "completedBefore":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE)) {
                            listCompletedBefore.add(qCompletedBefore.getId());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : questP.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.STARTED)) {
                            listStarted.add(qStarted.getId());
                        }
                        return String.join(separator, listStarted);
                }
            }
            return "null";
        }

        if (key[0].startsWith("quest:") || key[0].startsWith("q:")) {
            Quest quest = this.plugin.getQuestManager().getQuestById(key[0].substring(key[0].lastIndexOf(":") + 1));
            if (key.length < 2) {
                if (quest != null) {
                    return quest.getId();
                } else {
                    return "null";
                }
            } else if (key[1].equals("started") || key[1].equals("s")) {
                if (quest != null && questP.getQuestProgressFile().getQuestProgress(quest).isStarted()) {
                    return "true";
                }
                return "false";
            } else if (key[1].equals("completed") || key[1].equals("c")) {
                if (quest != null && questP.getQuestProgressFile().getQuestProgress(quest).isCompleted()) {
                    return "true";
                }
                return "false";
            } else if (key[1].equals("completedbefore") || key[1].equals("completedBefore") || key[1].equals("cB")) {
                if (quest != null && questP.getQuestProgressFile().getQuestProgress(quest).isCompletedBefore()) {
                    return "true";
                }
                return "false";
            } else if (key[1].equals("completiondate") || key[1].equals("completionDate")) {
                if (quest != null && questP.getQuestProgressFile().getQuestProgress(quest).isCompleted()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //TODO make configurable for all our american friends
                    return sdf.format(questP.getQuestProgressFile().getQuestProgress(quest).getCompletionDate());
                }
                return "Never";
            } else if (key[1].equals("cooldown")) {
                if (quest != null && questP.getQuestProgressFile().getQuestProgress(quest).isCompleted()) {
                    String time = this.plugin.convertToFormat(TimeUnit.SECONDS.convert(questP.getQuestProgressFile().getCooldownFor(quest), TimeUnit.MILLISECONDS));
                    if (time.startsWith("-")) {
                        return "0";
                    }
                    return time;
                }
                return "0";
            } else if (key[1].equals("canaccept") || key[1].equals("canAccept")) {
                if (quest != null && questP.getQuestProgressFile().canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                    return "true";
                }
                return "false";
            } else if (key[1].equals("meetsrequirements") || key[1].equals("meetsRequirements")) {
                if (quest != null && questP.getQuestProgressFile().hasMetRequirements(quest)) {
                    return "true";
                }
                return "false";
            } else if (key[1].startsWith("task") || key[1].startsWith("t")) {
                String[] t = key[1].split(":");
                if (key[2].equals("progress") || key[2].equals("p")) {
                    if (quest == null || questP.getQuestProgressFile().getQuestProgress(quest).getTaskProgress(t[1]).getProgress() == null) {
                        return "0";
                    }
                    return String.valueOf(questP.getQuestProgressFile().getQuestProgress(quest).getTaskProgress(t[1]).getProgress());
                } else if (key[2].equals("completed") || key[2].equals("c")) {
                    if (quest == null || questP.getQuestProgressFile().getQuestProgress(quest).getTaskProgress(t[1]).isCompleted()) {
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
            Category category = this.plugin.getQuestManager().getCategoryById(key[0].substring(key[0].lastIndexOf(":") + 1));
            if (key.length < 2) {
                if (category != null) {
                    return category.getId();
                } else {
                    return "null";
                }
            } else if (key.length == 2) {
                switch (key[1]) {
                    case "all":
                    case "a":
                        return String.valueOf(category.getRegisteredQuestIds().size());
                    case "completed":
                    case "c":
                        return String.valueOf(getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED).size());
                    case "completedbefore":
                    case "completedBefore":
                    case "cB":
                        return String.valueOf(getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE).size());
                    case "started":
                    case "s":
                        return String.valueOf(getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.STARTED).size());
                }
            } else if (key[2].equals("list") || key[2].equals("l")) {
                String separator = ",";
                if (!(key.length == 3)) {
                    separator = key[3];
                }

                switch (key[1]) {
                    case "all":
                    case "a":
                        List<String> listAll = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.ALL)) {
                            listAll.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listAll);
                    case "completed":
                    case "c":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED)) {
                            listCompleted.add(qCompleted.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompleted);
                    case "completedbefore":
                    case "completedBefore":
                    case "cB":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE)) {
                            listCompletedBefore.add(qCompletedBefore.getDisplayNameStripped());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                    case "s":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.STARTED)) {
                            listStarted.add(qStarted.getDisplayNameStripped());
                        }
                        return String.join(separator, listStarted);
                }
            } else if (key[2].equals("listid") || key[2].equals("lid")) {
                String separator = ",";
                if (!(key.length == 3)) {
                    separator = key[3];
                }

                switch (key[1]) {
                    case "all":
                    case "a":
                        List<String> listAll = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.ALL)) {
                            listAll.add(qCompleted.getId());
                        }
                        return String.join(separator, listAll);
                    case "completed":
                    case "c":
                        List<String> listCompleted = new ArrayList<>();
                        for (Quest qCompleted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED)) {
                            listCompleted.add(qCompleted.getId());
                        }
                        return String.join(separator, listCompleted);
                    case "completedbefore":
                    case "completedBefore":
                    case "cB":
                        List<String> listCompletedBefore = new ArrayList<>();
                        for (Quest qCompletedBefore : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE)) {
                            listCompletedBefore.add(qCompletedBefore.getId());
                        }
                        return String.join(separator, listCompletedBefore);
                    case "started":
                    case "s":
                        List<String> listStarted = new ArrayList<>();
                        for (Quest qStarted : getCategoryQuests(questP, category, QuestProgressFile.QuestsProgressFilter.STARTED)) {
                            listStarted.add(qStarted.getId());
                        }
                        return String.join(separator, listStarted);
                }
            }
            return "null";
        }
        return null;
    }

    private List<Quest> getCategoryQuests(QPlayer questP, Category category, QuestProgressFile.QuestsProgressFilter filter) {
        List<Quest> categoryQuests = new ArrayList<>();
        for (String cQuests : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(cQuests);
            if (quest == null) continue;

            boolean condition = false;
            if (filter == QuestProgressFile.QuestsProgressFilter.STARTED) {
                condition = questP.getQuestProgressFile().getQuestProgress(quest).isStarted();
            } else if (filter == QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE) {
                condition = questP.getQuestProgressFile().getQuestProgress(quest).isCompletedBefore();
            } else if (filter == QuestProgressFile.QuestsProgressFilter.COMPLETED) {
                condition = questP.getQuestProgressFile().getQuestProgress(quest).isCompleted();
            } else if (filter == QuestProgressFile.QuestsProgressFilter.ALL) {
                condition = true;
            }

            if (condition) {
                categoryQuests.add(quest);
            }
        }
        return categoryQuests;
    }
}
