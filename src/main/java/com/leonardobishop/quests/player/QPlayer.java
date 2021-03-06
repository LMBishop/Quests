package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.menu.CategoryQMenu;
import com.leonardobishop.quests.menu.QuestQMenu;
import com.leonardobishop.quests.menu.QuestSortWrapper;
import com.leonardobishop.quests.menu.StartedQMenu;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Options;
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

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile, Quests plugin) {
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
    public int openCategory(Category category, CategoryQMenu superMenu, boolean backButton) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        // Using `this` instead of searching again for this QPlayer
        QuestQMenu questQMenu = new QuestQMenu(plugin, this, category.getId(), superMenu);
        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        questQMenu.populate(quests);
        questQMenu.setBackButtonEnabled(backButton);
        return openCategory(category, questQMenu);
    }

    /**
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openCategory(Category category, QuestQMenu questQMenu) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        plugin.getMenuController().openMenu(player, questQMenu, 1);
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
            CategoryQMenu categoryQMenu = new CategoryQMenu(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()));
            List<QuestQMenu> questMenus = new ArrayList<>();
            for (Category category : plugin.getQuestManager().getCategories()) {
                QuestQMenu questQMenu = new QuestQMenu(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), categoryQMenu);
                List<Quest> quests = new ArrayList<>();
                for (String questid : category.getRegisteredQuestIds()) {
                    Quest quest = plugin.getQuestManager().getQuestById(questid);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
                questQMenu.populate(quests);
                questMenus.add(questQMenu);
            }
            categoryQMenu.populate(questMenus);

            plugin.getMenuController().openMenu(player, categoryQMenu, 1);
        } else {
            QuestQMenu questQMenu = new QuestQMenu(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()), "", null);
            List<Quest> quests = new ArrayList<>();
            for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                quests.add(entry.getValue());
            }
            questQMenu.populate(quests);
            questQMenu.setBackButtonEnabled(false);

            plugin.getMenuController().openMenu(player, questQMenu, 1);
        }
    }

    public void openStartedQuests() {
        if (this.uuid == null) {
            return;
        }
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        StartedQMenu startedQMenu = new StartedQMenu(plugin, plugin.getPlayerManager().getPlayer(player.getUniqueId()));
        List<QuestSortWrapper> quests = new ArrayList<>();
        for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
            quests.add(new QuestSortWrapper(plugin, entry.getValue()));
        }
        startedQMenu.populate(quests);

        plugin.getMenuController().openMenu(player, startedQMenu, 1);
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
