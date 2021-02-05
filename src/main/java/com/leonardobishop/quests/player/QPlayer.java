package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.obj.misc.QMenuCategory;
import com.leonardobishop.quests.obj.misc.QMenuQuest;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QPlayer {

    private final UUID uuid;
    private final QuestProgressFile questProgressFile;
    private final Quests plugin;

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile,  Quests plugin) {
        this.uuid = uuid;
        this.questProgressFile = questProgressFile;
        this.plugin = plugin;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openCategory(Category category, QMenuCategory superMenu, boolean backButton) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        // Using `this` instead of searching again for this QPlayer
        QMenuQuest qMenuQuest = new QMenuQuest(plugin, this, category.getId(), superMenu);
        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        qMenuQuest.populate(quests);
        qMenuQuest.setBackButtonEnabled(backButton);
        return openCategory(category, qMenuQuest);
    }

    /**
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openCategory(Category category, QMenuQuest qMenuQuest) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        player.openInventory(qMenuQuest.toInventory(1));
        EventInventory.track(this.uuid, qMenuQuest);
        return 0;
    }

    public void openQuests() {
        if (this.uuid == null) {
            return;
        }
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
            QMenuCategory qMenuCategory = new QMenuCategory(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()));
            List<QMenuQuest> questMenus = new ArrayList<>();
            for (Category category : plugin.getQuestManager().getCategories()) {
                QMenuQuest qMenuQuest = new QMenuQuest(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), qMenuCategory);
                List<Quest> quests = new ArrayList<>();
                for (String questid : category.getRegisteredQuestIds()) {
                    Quest quest = plugin.getQuestManager().getQuestById(questid);
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
            QMenuQuest qMenuQuest = new QMenuQuest(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()), "", null);
            List<Quest> quests = new ArrayList<>();
            for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                quests.add(entry.getValue());
            }
            qMenuQuest.populate(quests);
            qMenuQuest.setBackButtonEnabled(false);

            player.openInventory(qMenuQuest.toInventory(1));
            EventInventory.track(player.getUniqueId(), qMenuQuest);
        }
    }

    public QuestProgressFile getQuestProgressFile() {
        return questProgressFile;
    }

    public QuestProgressFile setQuestProgressFile() {
        return questProgressFile;
    }

    @Override //Used by java GC
    public boolean equals(Object o) {
        if (!(o instanceof QPlayer)) return false;
        QPlayer qPlayer = (QPlayer) o;
        return this.uuid == qPlayer.getUuid();
    }

    @Override //Used by java GC
    public int hashCode() {
        return uuid.hashCode() * 73; //uuid hash * prime number
    }
}
