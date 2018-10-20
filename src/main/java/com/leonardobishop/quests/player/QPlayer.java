package com.leonardobishop.quests.player;

import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.obj.misc.QMenuCategory;
import com.leonardobishop.quests.obj.misc.QMenuQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QPlayer {

    private UUID uuid;
    private QuestProgressFile questProgressFile;
    private boolean onlyDataLoaded;

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile) {
        this(uuid, questProgressFile, false);
    }

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile, boolean onlyDataLoaded) {
        this.uuid = uuid;
        this.questProgressFile = questProgressFile;
        this.onlyDataLoaded = onlyDataLoaded;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void openCategory(Category category) {
        if (onlyDataLoaded) {
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        QMenuQuest qMenuQuest = new QMenuQuest(Quests.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), null);
        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = Quests.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        qMenuQuest.populate(quests);
        qMenuQuest.setBackButtonEnabled(false);

        player.openInventory(qMenuQuest.toInventory(1));
        EventInventory.track(player.getUniqueId(), qMenuQuest);
    }

    public void openQuests() {
        if (onlyDataLoaded) {
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
            QMenuCategory qMenuCategory = new QMenuCategory(Quests.getPlayerManager().getPlayer(player.getUniqueId()));
            List<QMenuQuest> questMenus = new ArrayList<>();
            for (Category category : Quests.getQuestManager().getCategories()) {
                QMenuQuest qMenuQuest = new QMenuQuest(Quests.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), qMenuCategory);
                List<Quest> quests = new ArrayList<>();
                for (String questid : category.getRegisteredQuestIds()) {
                    Quest quest = Quests.getQuestManager().getQuestById(questid);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
                qMenuQuest.populate(quests);
                questMenus.add(qMenuQuest);
            }
            qMenuCategory.populate(questMenus);

            player.openInventory(qMenuCategory.toInventory(1));
            EventInventory.track(player.getUniqueId(), qMenuCategory);
        } else {
            QMenuQuest qMenuQuest = new QMenuQuest(Quests.getPlayerManager().getPlayer(player.getUniqueId()), "", null);
            List<Quest> quests = new ArrayList<>();
            for (Map.Entry<String, Quest> entry : Quests.getQuestManager().getQuests().entrySet()) {
                quests.add(entry.getValue());
            }
            qMenuQuest.populate(quests);
            qMenuQuest.setBackButtonEnabled(false);

            player.openInventory(qMenuQuest.toInventory(1));
            EventInventory.track(player.getUniqueId(), qMenuQuest);
        }
    }

    public boolean isOnlyDataLoaded() {
        return onlyDataLoaded;
    }

    public void setOnlyDataLoaded(boolean onlyDataLoaded) {
        this.onlyDataLoaded = onlyDataLoaded;
    }

    public QuestProgressFile getQuestProgressFile() {
        return questProgressFile;
    }

    public QuestProgressFile setQuestProgressFile() {
        return questProgressFile;
    }
}
