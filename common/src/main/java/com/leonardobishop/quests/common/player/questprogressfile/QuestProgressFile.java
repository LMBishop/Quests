package com.leonardobishop.quests.common.player.questprogressfile;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents underlying quest progress for a player.
 */
public class QuestProgressFile {

    private static final Constructor<?> optimizedMapCtor;

    static {
        Class<?> optimizedMapClazz;

        try {
            optimizedMapClazz = Class.forName("it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap");
        } catch (ClassNotFoundException ignored) {
            optimizedMapClazz = HashMap.class;
        }

        try {
            optimizedMapCtor = optimizedMapClazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> newOptimizedMapInstance() {
        try {
            return (Map<K, V>) optimizedMapCtor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Map<String, QuestProgress> questProgress = newOptimizedMapInstance();
    private final UUID playerUUID;
    private final Quests plugin;

    public QuestProgressFile(UUID playerUUID, Quests plugin) {
        this.playerUUID = playerUUID;
        this.plugin = plugin;
    }

    public QuestProgressFile(QuestProgressFile questProgressFile) {
        for (Map.Entry<String, QuestProgress> progressEntry : questProgressFile.questProgress.entrySet()) {
            questProgress.put(progressEntry.getKey(), new QuestProgress(progressEntry.getValue()));
        }
        this.playerUUID = questProgressFile.playerUUID;
        this.plugin = questProgressFile.plugin;
    }

    public void addQuestProgress(QuestProgress questProgress) {
        //TODO don't do here
//        if (Options.VERIFY_QUEST_EXISTS_ON_LOAD.getBooleanValue(true) && plugin.getQuestManager().getQuestById(questProgress.getQuestId()) == null) {
//            return;
//        }
        this.questProgress.put(questProgress.getQuestId(), questProgress);
    }

    /**
     * Gets all manually started quests.
     * Note: if quest autostart is enabled then this may produce unexpected results as quests are
     * not "started" by the player if autostart is true. Consider {@link QPlayer#hasStartedQuest(Quest)}
     * or {@link QPlayer#getEffectiveStartedQuests()} instead.
     *
     * @return list of started quests
     */
    public List<Quest> getStartedQuests() {
        List<Quest> startedQuests = new ArrayList<>();
        for (QuestProgress questProgress : questProgress.values()) {
            Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
            if (quest != null && questProgress.isStarted()) {
                startedQuests.add(plugin.getQuestManager().getQuestById(questProgress.getQuestId()));
            }
        }
        return startedQuests;
    }

    /**
     * Returns all {@link Quest} a player has encountered
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

        private final String legacy;

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

    /**
     * Checks whether or not the player has {@link QuestProgress} for a specified quest
     *
     * @param quest the quest to check for
     * @return true if they have quest progress
     */
    public boolean hasQuestProgress(Quest quest) {
        return questProgress.containsKey(quest.getId());
    }

    /**
     * Gets the remaining cooldown before being able to start a specific quest.
     *
     * @param quest the quest to test for
     * @return 0 if no cooldown remaining or the cooldown is disabled, otherwise the cooldown in milliseconds
     */
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

    /**
     * Gets the time remaining before a quest will have expired.
     *
     * @param quest the quest to test for
     * @return 0 if no time remaining, -1 if the time limit is disabled or the quest is not started,
     * otherwise the time left in milliseconds
     */
    public long getTimeRemainingFor(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        if (quest.isTimeLimitEnabled() && questProgress.isStarted()) {
            return Math.max(
                    questProgress.getStartedDate()
                            + TimeUnit.MILLISECONDS.convert(quest.getTimeLimit(), TimeUnit.MINUTES)
                            - System.currentTimeMillis()
                    , 0);
        }
        return -1;
    }

    /**
     * Tests whether or not the player meets the requirements to start a specific quest.
     *
     * @param quest the quest to test for
     * @return true if they can start the quest
     */
    //TODO possibly move this
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

    /**
     * Get the {@link UUID} of the player this QuestProgressFile represents.
     *
     * @return UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Get the {@link QuestProgress} for a specified {@link Quest}. Generates a new one if it does not exist.
     *
     * @param quest the quest to get progress for
     * @return {@link QuestProgress} or a blank generated one if the quest does not exist
     */
    public QuestProgress getQuestProgress(Quest quest) {
        if (questProgress.containsKey(quest.getId())) {
            return questProgress.get(quest.getId());
        }
        generateBlankQuestProgress(quest);
        return getQuestProgress(quest);
    }

    /**
     * Tests whether or not the player has a specified {@link Quest} started.
     *
     * @param quest the quest to check for
     * @return true if player has the quest started
     */
    public boolean hasQuestStarted(Quest quest) {
        QuestProgress qProgress = questProgress.get(quest.getId());
        return qProgress != null && qProgress.isStarted();
    }

    /**
     * Generate a new blank {@link QuestProgress} for a specified {@code quest}.
     *
     * @param quest the quest to generate progress for
     */
    public void generateBlankQuestProgress(Quest quest) {
        generateBlankQuestProgress(quest, false);
    }

    /**
     * Generate a new blank {@link QuestProgress} for a specified {@code quest}.
     *
     * @param quest the quest to generate progress for
     * @param modified the modified state of the quest
     */
    public void generateBlankQuestProgress(Quest quest, boolean modified) {
        QuestProgress questProgress = new QuestProgress(plugin, quest.getId(), false, false, 0, playerUUID, false, 0, modified);
        for (Task task : quest.getTasks()) {
            TaskProgress taskProgress = new TaskProgress(questProgress, task.getId(), null, playerUUID, false, modified);
            questProgress.addTaskProgress(taskProgress);
        }

        addQuestProgress(questProgress);
    }

    public void clear() {
        questProgress.clear();
    }

    /**
     * Reset quests to their default state. More specifically, this will reset all
     * quest progress with non-default parameters back to default and only
     * set the modified flag in that case.
     */
    public void reset() {
        for (QuestProgress questProgress : questProgress.values()) {
            if (!questProgress.hasNonDefaultValues()) {
                continue;
            }
            Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
            if (quest == null) {
                continue;
            }
            generateBlankQuestProgress(quest, true);
        }
    }

    /**
     * Removes any references to quests or tasks which are no longer defined in the config.
     */
    @Deprecated
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

    public void resetModified() {
        for (QuestProgress questProgress : questProgress.values()) {
            questProgress.resetModified();
        }
    }

    public void setModified(boolean modified) {
        for (QuestProgress questProgress : questProgress.values()) {
            questProgress.setModified(modified);
        }
    }

}
