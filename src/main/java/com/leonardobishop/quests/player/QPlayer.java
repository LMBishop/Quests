package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.api.events.PlayerCancelQuestEvent;
import com.leonardobishop.quests.api.events.PlayerFinishQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStartQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStartTrackQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStopTrackQuestEvent;
import com.leonardobishop.quests.api.events.PreStartQuestEvent;
import com.leonardobishop.quests.menu.CategoryQMenu;
import com.leonardobishop.quests.menu.QuestQMenu;
import com.leonardobishop.quests.menu.QuestSortWrapper;
import com.leonardobishop.quests.menu.StartedQMenu;
import com.leonardobishop.quests.player.questprogressfile.QPlayerPreferences;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a player.
 */
public class QPlayer {

    private final UUID uuid;
    private final QPlayerPreferences playerPreferences;
    private final QuestProgressFile questProgressFile;
    private final Quests plugin;

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile, QPlayerPreferences playerPreferences, Quests plugin) {
        this.uuid = uuid;
        this.playerPreferences = playerPreferences;
        this.questProgressFile = questProgressFile;
        this.plugin = plugin;
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
        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        if (Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue() && !(quest.isRepeatable() && !quest.isCooldownEnabled())) {
            trackQuest(null);
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            QPlayer questPlayer = QuestsAPI.getPlayerManager().getPlayer(uuid);
            String questFinishMessage = Messages.QUEST_COMPLETE.getMessage().replace("{quest}", quest.getDisplayNameStripped());
            // PlayerFinishQuestEvent -- start
            PlayerFinishQuestEvent questFinishEvent = new PlayerFinishQuestEvent(player, questPlayer, questProgress, questFinishMessage);
            Bukkit.getPluginManager().callEvent(questFinishEvent);
            // PlayerFinishQuestEvent -- end
            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                for (String s : quest.getRewards()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName())); //TODO PlaceholderAPI support
                }
            });
            if (questFinishEvent.getQuestFinishMessage() != null)
                player.sendMessage(questFinishEvent.getQuestFinishMessage());
            if (Options.TITLES_ENABLED.getBooleanValue()) {
                plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()), Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()));
            }
            for (String s : quest.getRewardString()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        return true;
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
        if (Options.QUEST_AUTOSTART.getBooleanValue()) {
            QuestStartResult response = canStartQuest(quest);
            return response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED;
        } else {
            return questProgressFile.hasQuestProgress(quest) && questProgressFile.getQuestProgress(quest).isStarted();
        }
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
        Player player = Bukkit.getPlayer(uuid);
        QuestStartResult code = canStartQuest(quest);
        if (player != null) {
            String questResultMessage = null;
            switch (code) {
                case QUEST_SUCCESS:
                    // This one is hacky
                    break;
                case QUEST_LIMIT_REACHED:
                    questResultMessage = Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(Options.QUESTS_START_LIMIT.getIntValue()));
                    break;
                case QUEST_ALREADY_COMPLETED:
                    questResultMessage = Messages.QUEST_START_DISABLED.getMessage();
                    break;
                case QUEST_COOLDOWN:
                    long cooldown = questProgressFile.getCooldownFor(quest);
                    questResultMessage = Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", String.valueOf(plugin.convertToFormat(TimeUnit.SECONDS.convert
                            (cooldown, TimeUnit.MILLISECONDS))));
                    break;
                case QUEST_LOCKED:
                    questResultMessage = Messages.QUEST_START_LOCKED.getMessage();
                    break;
                case QUEST_ALREADY_STARTED:
                    questResultMessage = Messages.QUEST_START_STARTED.getMessage();
                    break;
                case QUEST_NO_PERMISSION:
                    questResultMessage = Messages.QUEST_START_PERMISSION.getMessage();
                    break;
                case NO_PERMISSION_FOR_CATEGORY:
                    questResultMessage = Messages.QUEST_CATEGORY_QUEST_PERMISSION.getMessage();
                    break;
            }
            // PreStartQuestEvent -- start
            PreStartQuestEvent preStartQuestEvent = new PreStartQuestEvent(player, this, questResultMessage, code);
            Bukkit.getPluginManager().callEvent(preStartQuestEvent);
            // PreStartQuestEvent -- end
            if (preStartQuestEvent.getQuestResultMessage() != null && code != QuestStartResult.QUEST_SUCCESS)
                player.sendMessage(preStartQuestEvent.getQuestResultMessage());
        }
        if (code == QuestStartResult.QUEST_SUCCESS) {
            QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
            questProgress.setStarted(true);
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                taskProgress.setCompleted(false);
                taskProgress.setProgress(null);
            }
            if (Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue()) {
                trackQuest(quest);
            }
            questProgress.setCompleted(false);
            if (player != null) {
                String questStartMessage = Messages.QUEST_START.getMessage().replace("{quest}", quest.getDisplayNameStripped());
                // PlayerStartQuestEvent -- start
                PlayerStartQuestEvent questStartEvent = new PlayerStartQuestEvent(player, this, questProgress, questStartMessage);
                Bukkit.getPluginManager().callEvent(questStartEvent);
                // PlayerStartQuestEvent -- end
                if (questStartEvent.getQuestStartMessage() != null)
                    player.sendMessage(questStartEvent.getQuestStartMessage()); //Don't send a message if the event message is null
                if (Options.TITLES_ENABLED.getBooleanValue()) {
                    plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()), Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()));
                }
                for (String s : quest.getStartString()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            for (Task task : quest.getTasks()) {
                try {
                    plugin.getTaskTypeManager().getTaskType(task.getType()).onStart(quest, task, uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }

    /**
     * Attempt to cancel a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was cancelled, false otherwise
     */
    public boolean cancelQuest(Quest quest) {
        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
        Player player = Bukkit.getPlayer(uuid);
        if (!questProgress.isStarted()) {
            if (player != null) {
                player.sendMessage(Messages.QUEST_CANCEL_NOTSTARTED.getMessage());
            }
            return false;
        }
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setProgress(null);
        }
        if (player != null) {
            String questCancelMessage = Messages.QUEST_CANCEL.getMessage().replace("{quest}", quest.getDisplayNameStripped());
            // PlayerCancelQuestEvent -- start
            PlayerCancelQuestEvent questCancelEvent = new PlayerCancelQuestEvent(player, this, questProgress, questCancelMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            if (questCancelEvent.getQuestCancelMessage() != null)
                player.sendMessage(questCancelEvent.getQuestCancelMessage());
        }
        return true;
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
        Player p = Bukkit.getPlayer(uuid);
        if (questProgressFile.getStartedQuests().size() >= Options.QUESTS_START_LIMIT.getIntValue() && !Options.QUEST_AUTOSTART.getBooleanValue()) {
            return QuestStartResult.QUEST_LIMIT_REACHED;
        }
        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
        if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
            //if (playerUUID != null) {
            // ???
            //}
            return QuestStartResult.QUEST_ALREADY_COMPLETED;
        }
        long cooldown = questProgressFile.getCooldownFor(quest);
        if (cooldown > 0) {
            return QuestStartResult.QUEST_COOLDOWN;
        }
        if (!questProgressFile.hasMetRequirements(quest)) {
            return QuestStartResult.QUEST_LOCKED;
        }
        if (questProgress.isStarted()) {
            return QuestStartResult.QUEST_ALREADY_STARTED;
        }
        if (quest.isPermissionRequired()) {
            if (p != null) {
                if (!p.hasPermission("quests.quest." + quest.getId())) {
                    return QuestStartResult.QUEST_NO_PERMISSION;
                }
            } else {
                return QuestStartResult.QUEST_NO_PERMISSION;
            }
        }
        if (quest.getCategoryId() != null && plugin.getQuestManager().getCategoryById(quest.getCategoryId()) != null && plugin.getQuestManager()
                .getCategoryById(quest.getCategoryId()).isPermissionRequired()) {
            if (p != null) {
                if (!p.hasPermission("quests.category." + quest.getCategoryId())) {
                    return QuestStartResult.NO_PERMISSION_FOR_CATEGORY;
                }
            } else {
                return QuestStartResult.NO_PERMISSION_FOR_CATEGORY;
            }
        }
        return QuestStartResult.QUEST_SUCCESS;
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

    public QPlayerPreferences getPlayerPreferences() {
        return playerPreferences;
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
