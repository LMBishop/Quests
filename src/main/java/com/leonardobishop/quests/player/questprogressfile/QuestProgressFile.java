package com.leonardobishop.quests.player.questprogressfile;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.api.events.PlayerCancelQuestEvent;
import com.leonardobishop.quests.api.events.PlayerFinishQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStartQuestEvent;
import com.leonardobishop.quests.api.events.PreStartQuestEvent;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuestProgressFile {

    private final Map<String, QuestProgress> questProgress = new HashMap<>();
    private final UUID playerUUID;
    private final Quests plugin;

    public QuestProgressFile(UUID player, Quests plugin) {
        this.playerUUID = player;
        this.plugin = plugin;
    }

    public boolean completeQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        Player player = Bukkit.getPlayer(this.playerUUID);
        if (player != null) {
            QPlayer questPlayer = QuestsAPI.getPlayerManager().getPlayer(this.playerUUID);
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
                plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
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
     * Check if the player can start a quest.
     * <p>
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return the quest start result
     */
    public QuestStartResult canStartQuest(Quest quest) {
        Player p = Bukkit.getPlayer(playerUUID);
        if (getStartedQuests().size() >= Options.QUESTS_START_LIMIT.getIntValue() && !Options.QUEST_AUTOSTART.getBooleanValue()) {
            return QuestStartResult.QUEST_LIMIT_REACHED;
        }
        QuestProgress questProgress = getQuestProgress(quest);
        if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
            //if (playerUUID != null) {
            // ???
            //}
            return QuestStartResult.QUEST_ALREADY_COMPLETED;
        }
        long cooldown = getCooldownFor(quest);
        if (cooldown > 0) {
            return QuestStartResult.QUEST_COOLDOWN;
        }
        if (!hasMetRequirements(quest)) {
            return QuestStartResult.QUEST_LOCKED;
        }
        if (questProgress.isStarted()) {
            return QuestStartResult.QUEST_ALREADY_STARTED;
        }
        if (quest.isPermissionRequired()) {
            if (playerUUID != null) {
                if (!p.hasPermission("quests.quest." + quest.getId())) {
                    return QuestStartResult.QUEST_NO_PERMISSION;
                }
            } else {
                return QuestStartResult.QUEST_NO_PERMISSION;
            }
        }
        if (quest.getCategoryId() != null && plugin.getQuestManager().getCategoryById(quest.getCategoryId()) != null && plugin.getQuestManager()
                .getCategoryById(quest.getCategoryId()).isPermissionRequired()) {
            if (playerUUID != null) {
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
     * Start a quest for the player.
     * <p>
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return the quest start result
     */
    // TODO PlaceholderAPI support
    public QuestStartResult startQuest(Quest quest) {
        Player player = Bukkit.getPlayer(playerUUID);
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
                    long cooldown = getCooldownFor(quest);
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
            QPlayer questPlayer = QuestsAPI.getPlayerManager().getPlayer(this.playerUUID);
            // PreStartQuestEvent -- start
            PreStartQuestEvent preStartQuestEvent = new PreStartQuestEvent(player, questPlayer, questResultMessage, code);
            Bukkit.getPluginManager().callEvent(preStartQuestEvent);
            // PreStartQuestEvent -- end
            if (preStartQuestEvent.getQuestResultMessage() != null && code != QuestStartResult.QUEST_SUCCESS)
                player.sendMessage(preStartQuestEvent.getQuestResultMessage());
        }
        if (code == QuestStartResult.QUEST_SUCCESS) {
            QuestProgress questProgress = getQuestProgress(quest);
            questProgress.setStarted(true);
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                taskProgress.setCompleted(false);
                taskProgress.setProgress(null);
            }
            questProgress.setCompleted(false);
            if (player != null) {
                QPlayer questPlayer = QuestsAPI.getPlayerManager().getPlayer(this.playerUUID);
                String questStartMessage = Messages.QUEST_START.getMessage().replace("{quest}", quest.getDisplayNameStripped());
                // PlayerStartQuestEvent -- start
                PlayerStartQuestEvent questStartEvent = new PlayerStartQuestEvent(player, questPlayer, questProgress, questStartMessage);
                Bukkit.getPluginManager().callEvent(questStartEvent);
                // PlayerStartQuestEvent -- end
                if (questStartEvent.getQuestStartMessage() != null)
                    player.sendMessage(questStartEvent.getQuestStartMessage()); //Don't send a message if the event message is null
                if (Options.TITLES_ENABLED.getBooleanValue()) {
                    plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()), Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()));
                }
                for (String s : quest.getStartString()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            for (Task task : quest.getTasks()) {
                try {
                    plugin.getTaskTypeManager().getTaskType(task.getType()).onStart(quest, task, playerUUID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }

    public boolean cancelQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        Player player = Bukkit.getPlayer(this.playerUUID);
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
            QPlayer questPlayer = QuestsAPI.getPlayerManager().getPlayer(this.playerUUID);
            String questCancelMessage = Messages.QUEST_CANCEL.getMessage().replace("{quest}", quest.getDisplayNameStripped());
            // PlayerCancelQuestEvent -- start
            PlayerCancelQuestEvent questCancelEvent = new PlayerCancelQuestEvent(player, questPlayer, questProgress, questCancelMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            if (questCancelEvent.getQuestCancelMessage() != null)
                player.sendMessage(questCancelEvent.getQuestCancelMessage());
        }
        return true;
    }

    public void addQuestProgress(QuestProgress questProgress) {
        this.questProgress.put(questProgress.getQuestId(), questProgress);
    }

    public List<Quest> getStartedQuests() {
        List<Quest> startedQuests = new ArrayList<>();
        for (QuestProgress questProgress : questProgress.values()) {
            if (questProgress.isStarted()) {
                startedQuests.add(plugin.getQuestManager().getQuestById(questProgress.getQuestId()));
            }
        }
        return startedQuests;
    }

    /**
     * @return {@code List<Quest>} all quest
     * @deprecated use {@code getAllQuestsFromProgress(QuestsProgressFilter)} instead
     * <p>
     * Returns all {@code Quest}s a player has encountered
     * (not to be confused with a collection of quest progress)
     */
    @Deprecated
    public List<Quest> getQuestsProgress(String filter) {
        return getAllQuestsFromProgress(QuestsProgressFilter.fromLegacy(filter));
    }

    /**
     * Returns all {@code Quest}s a player has encountered
     * (not to be confused with a collection of quest progress)
     *
     * @return {@code List<Quest>} all quests
     */
    public List<Quest> getAllQuestsFromProgress(QuestsProgressFilter filter) {
        List<Quest> questsProgress = new ArrayList<>();
        for (QuestProgress qProgress : questProgress.values()) {
            boolean condition = false;
            if (filter == QuestsProgressFilter.STARTED) {
                condition = qProgress.isStarted();
            } else if (filter == QuestsProgressFilter.COMPLETED_BEFORE) {
                condition = qProgress.isCompletedBefore();
            } else if (filter == QuestsProgressFilter.COMPLETED) {
                condition = qProgress.isCompleted();
            } else if (filter == QuestsProgressFilter.ALL) {
                condition = true;
            }
            if (condition) {
                Quest quest = plugin.getQuestManager().getQuestById(qProgress.getQuestId());
                if (quest != null) {
                    questsProgress.add(quest);
                }
            }
        }
        return questsProgress;
    }

    public enum QuestsProgressFilter {
        ALL("all"),
        COMPLETED("completed"),
        COMPLETED_BEFORE("completedBefore"),
        STARTED("started");

        private String legacy;

        QuestsProgressFilter(String legacy) {
            this.legacy = legacy;
        }

        public static QuestsProgressFilter fromLegacy(String filter) {
            for (QuestsProgressFilter filterEnum : QuestsProgressFilter.values()) {
                if (filterEnum.getLegacy().equals(filter)) return filterEnum;
            }
            return QuestsProgressFilter.ALL;
        }

        public String getLegacy() {
            return legacy;
        }
    }

    /**
     * Gets all the quest progress that it has ever encountered.
     *
     * @return {@code Collection<QuestProgress>} all quest progresses
     */
    public Collection<QuestProgress> getAllQuestProgress() {
        return questProgress.values();
    }

    public boolean hasQuestProgress(Quest quest) {
        return questProgress.containsKey(quest.getId());
    }

    public boolean hasStartedQuest(Quest quest) {
        if (Options.QUEST_AUTOSTART.getBooleanValue()) {
            QuestStartResult response = canStartQuest(quest);
            return response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED;
        } else {
            return hasQuestProgress(quest) && getQuestProgress(quest).isStarted();
        }
    }

    public long getCooldownFor(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        if (quest.isCooldownEnabled() && questProgress.isCompleted()) {
            if (questProgress.getCompletionDate() > 0) {
                long date = questProgress.getCompletionDate();
                return (date + TimeUnit.MILLISECONDS.convert(quest.getCooldown(), TimeUnit.MINUTES)) - System.currentTimeMillis();
            }
        }
        return 0;
    }

    public boolean hasMetRequirements(Quest quest) {
        for (String id : quest.getRequirements()) {
            Quest q = plugin.getQuestManager().getQuestById(id);
            if (q == null) {
                continue;
            }
            if (hasQuestProgress(q) && !getQuestProgress(q).isCompletedBefore()) {
                return false;
            } else if (!hasQuestProgress(q)) {
                return false;
            }
        }
        return true;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public QuestProgress getQuestProgress(Quest quest) {
        if (questProgress.containsKey(quest.getId())) {
            return questProgress.get(quest.getId());
        } else if (generateBlankQuestProgress(quest.getId())) {
            return getQuestProgress(quest);
        }
        return null;
    }

    public boolean generateBlankQuestProgress(String questid) {
        if (plugin.getQuestManager().getQuestById(questid) != null) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            QuestProgress questProgress = new QuestProgress(plugin, quest.getId(), false, false, 0, playerUUID, false, false);
            for (Task task : quest.getTasks()) {
                TaskProgress taskProgress = new TaskProgress(questProgress, task.getId(), null, playerUUID, false, false);
                questProgress.addTaskProgress(taskProgress);
            }

            addQuestProgress(questProgress);
            return true;
        }
        return false;
    }

    public void saveToDisk() {
        saveToDisk(false, false);
    }

    public void saveToDisk(boolean disable) {
        saveToDisk(disable, false);
    }

    public void saveToDisk(boolean disable, boolean fullWrite) {
        plugin.getQuestsLogger().debug("Saving player " + playerUUID + " to disk.");
        File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdirs();
        }
        File file = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + playerUUID.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("quest-progress", null);
        for (QuestProgress questProgress : questProgress.values()) {
            data.set("quest-progress." + questProgress.getQuestId() + ".started", questProgress.isStarted());
            data.set("quest-progress." + questProgress.getQuestId() + ".completed", questProgress.isCompleted());
            data.set("quest-progress." + questProgress.getQuestId() + ".completed-before", questProgress.isCompletedBefore());
            data.set("quest-progress." + questProgress.getQuestId() + ".completion-date", questProgress.getCompletionDate());
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".completed", taskProgress
                        .isCompleted());
                data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".progress", taskProgress
                        .getProgress());
            }
        }

        try {
            data.save(file);
            if (disable)
                synchronized (this.questProgress) {
                    for (QuestProgress questProgress : questProgress.values()) {
                        questProgress.resetModified();
                    }
                }
            else
                for (QuestProgress questProgress : questProgress.values()) {
                    questProgress.resetModified();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        questProgress.clear();
    }

    /**
     * Removes any references to quests or tasks which are no longer defined in the config
     */
    public void clean() {
        plugin.getQuestsLogger().debug("Cleaning file " + playerUUID + ".");
        if (!plugin.getTaskTypeManager().areRegistrationsAccepted()) {
            ArrayList<String> invalidQuests = new ArrayList<>();
            for (String questId : this.questProgress.keySet()) {
                Quest q;
                if ((q = plugin.getQuestManager().getQuestById(questId)) == null) {
                    invalidQuests.add(questId);
                } else {
                    ArrayList<String> invalidTasks = new ArrayList<>();
                    for (String taskId : this.questProgress.get(questId).getTaskProgressMap().keySet()) {
                        if (q.getTaskById(taskId) == null) {
                            invalidTasks.add(taskId);
                        }
                    }
                    for (String taskId : invalidTasks) {
                        this.questProgress.get(questId).getTaskProgressMap().remove(taskId);
                    }
                }
            }
            for (String questId : invalidQuests) {
                this.questProgress.remove(questId);
            }
        }
    }

}
