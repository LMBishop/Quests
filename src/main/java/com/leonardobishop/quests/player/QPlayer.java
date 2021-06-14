package com.leonardobishop.quests.player;

import com.leonardobishop.quests.util.QuestMode;
import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.api.events.PlayerFinishQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStartTrackQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStopTrackQuestEvent;
import com.leonardobishop.quests.menu.CategoryQMenu;
import com.leonardobishop.quests.menu.DailyQMenu;
import com.leonardobishop.quests.menu.QuestQMenu;
import com.leonardobishop.quests.menu.QuestSortWrapper;
import com.leonardobishop.quests.menu.StartedQMenu;
import com.leonardobishop.quests.player.questprogressfile.QPlayerPreferences;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quest.Category;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.controller.QuestController;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player.
 */
public class QPlayer {

    private final Quests plugin;
    private final UUID uuid;
    private final QPlayerPreferences playerPreferences;
    private final QuestProgressFile questProgressFile;
    private QuestController questController;

    public QPlayer(Quests plugin, UUID uuid, QPlayerPreferences playerPreferences, QuestProgressFile questProgressFile, QuestController questController) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.playerPreferences = playerPreferences;
        this.questProgressFile = questProgressFile;
        this.questController = questController;
    }

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    /**
     * Attempt to complete a quest for the player. This will also play all effects (such as titles, messages etc.)
     * and also dispatches all rewards for the player.
     *
     * Warning: rewards will not be sent and the {@link PlayerFinishQuestEvent} will not be fired if the
     * player is not online
     *
     * @param quest the quest to complete
     * @return true (always)
     */
    public boolean completeQuest(Quest quest) {
        return questController.completeQuestForPlayer(this, quest);
    }

    /**
     * Attempt to track a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * Warning: {@link PlayerStopTrackQuestEvent} is not fired if the player is not online
     *
     * @param quest the quest to track
     */
    public void trackQuest(Quest quest) {
        Player player = Bukkit.getPlayer(uuid);
        if (quest == null) {
            String currentTrackedQuestId = playerPreferences.getTrackedQuestId();
            playerPreferences.setTrackedQuestId(null);
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerStopTrackQuestEvent(player, this));
                Quest currentTrackedQuest;
                if (currentTrackedQuestId != null && (currentTrackedQuest = plugin.getQuestManager().getQuestById(currentTrackedQuestId)) != null) {
                    player.sendMessage(Messages.QUEST_TRACK_STOP.getMessage().replace("{quest}", currentTrackedQuest.getDisplayNameStripped()));
                }
            }
        } else if (hasStartedQuest(quest)) {
            playerPreferences.setTrackedQuestId(quest.getId());
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerStartTrackQuestEvent(player, this));
                player.sendMessage(Messages.QUEST_TRACK.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
            }
        }
    }

    /**
     * Gets whether or not the player has started a specific quest.
     *
     * @param quest the quest to test for
     * @return true if the quest is started or quest autostart is enabled and the quest is ready to start, false otherwise
     */
    public boolean hasStartedQuest(Quest quest) {
        return questController.hasPlayerStartedQuest(this, quest);
    }

    /**
     * Attempt to start a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to start
     * @return the quest start result -- {@code QuestStartResult.QUEST_SUCCESS} indicates success
     */
    // TODO PlaceholderAPI support
    public QuestStartResult startQuest(Quest quest) {
        return questController.startQuestForPlayer(this, quest);
    }

    /**
     * Attempt to cancel a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was cancelled, false otherwise
     */
    public boolean cancelQuest(Quest quest) {
        return questController.cancelQuestForPlayer(this, quest);
    }

    /**
     * Check if the player can start a quest.
     *
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return the quest start result
     */
    public QuestStartResult canStartQuest(Quest quest) {
        return questController.canPlayerStartQuest(this, quest);
    }

    /**
     * Opens a category menu for the player.
     *
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
     * Opens a category menu for the player.
     *
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

        if (plugin.getQuestMode() == QuestMode.NORMAL) {
            if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
                CategoryQMenu categoryQMenu = new CategoryQMenu(plugin, this);
                List<QuestQMenu> questMenus = new ArrayList<>();
                for (Category category : plugin.getQuestManager().getCategories()) {
                    QuestQMenu questQMenu = new QuestQMenu(plugin, this, category.getId(), categoryQMenu);
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
                QuestQMenu questQMenu = new QuestQMenu(plugin, this, "", null);
                List<Quest> quests = new ArrayList<>();
                for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                    quests.add(entry.getValue());
                }
                questQMenu.populate(quests);
                questQMenu.setBackButtonEnabled(false);

                plugin.getMenuController().openMenu(player, questQMenu, 1);
            }
        } else {
            DailyQMenu dailyQMenu = new DailyQMenu(plugin, this);
            dailyQMenu.populate();
            plugin.getMenuController().openMenu(player, dailyQMenu, 1);
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

        StartedQMenu startedQMenu = new StartedQMenu(plugin, this);
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

    public QPlayerPreferences getPlayerPreferences() {
        return playerPreferences;
    }

    public QuestController getQuestController() {
        return questController;
    }

    public void setQuestController(QuestController questController) {
        this.questController = questController;
    }

    @Override //Used by java GC
    public boolean equals(Object o) {
        if (!(o instanceof QPlayer)) return false;
        QPlayer qPlayer = (QPlayer) o;
        return this.uuid == qPlayer.getPlayerUUID();
    }

    @Override //Used by java GC
    public int hashCode() {
        return uuid.hashCode() * 73; //uuid hash * prime number
    }
}
