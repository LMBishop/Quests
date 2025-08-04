package com.leonardobishop.quests.common.player.questprogressfile;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.filters.QuestProgressFilter;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Represents underlying quest progress for a player.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
public final class QuestProgressFile {

    // https://github.com/LMBishop/Quests/issues/760
    private static final boolean DEBUG_ISSUE_760 = Boolean.getBoolean("Quests.DebugIssue760");

    private final Quests plugin;
    private final UUID playerUUID;
    private final Map<String, QuestProgress> questProgressMap;

    /**
     * Constructs a QuestProgressFile.
     *
     * @param plugin     the plugin instance
     * @param playerUUID the associated player UUID
     */
    public QuestProgressFile(final Quests plugin, final UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.questProgressMap = HashMap.newHashMap(1024); // reduce collisions
    }

    /**
     * Constructs a data-only clone from a QuestProgressFile instance.
     *
     * @param questProgressFile the quest progress file instance
     */
    public QuestProgressFile(final QuestProgressFile questProgressFile) {
        final Set<Map.Entry<String, QuestProgress>> progressEntries = questProgressFile.questProgressMap.entrySet();

        this.plugin = questProgressFile.plugin;
        this.playerUUID = questProgressFile.playerUUID;
        this.questProgressMap = HashMap.newHashMap(progressEntries.size());

        for (final Map.Entry<String, QuestProgress> progressEntry : progressEntries) {
            this.questProgressMap.put(progressEntry.getKey(), new QuestProgress(progressEntry.getValue()));
        }
    }

    /**
     * @param questProgress the quest progress to put into the quest progress map
     */
    public void addQuestProgress(final QuestProgress questProgress) {
        // TODO don't do that here
        //if (Options.VERIFY_QUEST_EXISTS_ON_LOAD.getBooleanValue(true) && plugin.getQuestManager().getQuestById(questProgress.getQuestId()) == null) {
        //    return;
        //}
        this.questProgressMap.put(questProgress.getQuestId(), questProgress);
    }

    /**
     * Gets all manually started quests. If quest autostart is enabled then this may produce unexpected results as
     * quests are not "started" by the player if autostart is true. Consider {@link QPlayer#hasStartedQuest(Quest)}
     * or {@link QPlayer#getEffectiveStartedQuests()} usage instead.
     *
     * @return list of started quests
     */
    @Contract(pure = true)
    public List<Quest> getStartedQuests() {
        return this.getAllQuestsFromProgress(QuestProgressFilter.STARTED);
    }

    /**
     * Returns all {@link Quest} a player has encountered (not to be confused with a collection of quest progress).
     *
     * @return list of matching quests
     */
    @Contract(pure = true)
    public List<Quest> getAllQuestsFromProgress(final QuestProgressFilter filter) {
        final List<Quest> quests = new ArrayList<>();

        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            final Quest quest = this.getQuestFromProgress(filter, questProgress);

            if (quest != null) {
                quests.add(quest);
            }
        }

        return quests;
    }

    /**
     * Passes all {@link Quest} a player has encountered (not to be confused with a collection of quest progress) to specified consumer.
     */
    @Contract(pure = true)
    public void getAllQuestsFromProgressConsumer(final QuestProgressFilter filter, final Consumer<Quest> consumer) {
        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            final Quest quest = this.getQuestFromProgress(filter, questProgress);

            if (quest != null) {
                consumer.accept(quest);
            }
        }
    }

    /**
     * Returns count of all {@link Quest} a player has encountered. It is clearly equivalent to collection size of
     * {@link QuestProgressFile#getAllQuestsFromProgress(QuestProgressFilter)}, however it does not utilise a list
     * for counting quests so its performance is undeniably better.
     *
     * @return count of matching quests
     */
    @Contract(pure = true)
    public int getAllQuestsFromProgressCount(final QuestProgressFilter filter) {
        int count = 0;

        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            final Quest quest = this.getQuestFromProgress(filter, questProgress);

            if (quest != null) {
                count++;
            }
        }

        return count;
    }

    @Contract(pure = true)
    private @Nullable Quest getQuestFromProgress(final QuestProgressFilter filter, final QuestProgress questProgress) {
        final boolean matchesProgress = filter.matchesProgress(questProgress);
        if (!matchesProgress) {
            return null;
        }

        final Quest quest = this.plugin.getQuestManager().getQuestById(questProgress.getQuestId());
        if (quest == null) {
            return null;
        }

        final boolean matchesQuest = filter.matchesQuest(quest);
        if (!matchesQuest) {
            return null;
        }

        return quest;
    }

    /**
     * Gets map of quest id to quest progress.
     *
     * @return {@code Map<String, QuestProgress>} quest progress map
     */
    @Contract(pure = true)
    public Map<String, QuestProgress> getQuestProgressMap() {
        return this.questProgressMap;
    }

    /**
     * Gets all the quest progress that it has ever encountered.
     *
     * @return {@code Collection<QuestProgress>} all quest progresses
     */
    @Contract(pure = true)
    public Collection<QuestProgress> getAllQuestProgress() {
        return this.questProgressMap.values();
    }

    /**
     * Checks whether the player has {@link QuestProgress} for a specified quest
     *
     * @param quest the quest to check for
     * @return true if they have quest progress
     */
    @Contract(pure = true)
    public boolean hasQuestProgress(final Quest quest) {
        return this.questProgressMap.containsKey(quest.getId());
    }

    /**
     * Gets the remaining cooldown before being able to start a specific quest.
     *
     * @param quest the quest to test for
     * @return {@code 0} if no cooldown remaining, {@code -1} if the cooldown is disabled or the quest is not completed,
     * otherwise the cooldown in milliseconds
     */
    @Contract(pure = true)
    public long getCooldownFor(final Quest quest) {
        if (!quest.isCooldownEnabled()) {
            return -1;
        }

        final QuestProgress questProgress = this.getQuestProgressOrNull(quest);
        if (questProgress == null || !questProgress.isCompleted()) {
            return -1;
        }

        final long completionDate = questProgress.getCompletionDate();
        if (completionDate == 0L) {
            return -1;
        }

        final long currentTimeMillis = System.currentTimeMillis();
        final long cooldownMillis = TimeUnit.MILLISECONDS.convert(quest.getCooldown(), TimeUnit.MINUTES);

        // do the subtraction first to prevent overflow
        return Math.max(0L, completionDate - currentTimeMillis + cooldownMillis);
    }

    /**
     * Gets the time remaining before a quest will have expired.
     *
     * @param quest the quest to test for
     * @return {@code 0} if no time remaining, {@code -1} if the time limit is disabled or the quest is not started,
     * otherwise the time left in milliseconds
     */
    @Contract(pure = true)
    public long getTimeRemainingFor(final Quest quest) {
        if (!quest.isTimeLimitEnabled()) {
            return -1;
        }

        final QuestProgress questProgress = this.getQuestProgressOrNull(quest);
        if (questProgress == null || !questProgress.isStarted()) {
            return -1;
        }

        final long startedDate = questProgress.getStartedDate();
        if (startedDate == 0L) {
            return -1;
        }

        final long currentTimeMillis = System.currentTimeMillis();
        final long timeLimitMillis = TimeUnit.MILLISECONDS.convert(quest.getTimeLimit(), TimeUnit.MINUTES);

        // do the subtraction first to prevent overflow
        return Math.max(0L, startedDate - currentTimeMillis + timeLimitMillis);
    }

    /**
     * Tests whether the player meets the requirements to start a specific quest.
     *
     * @param quest the quest to test for
     * @return true if they can start the quest
     */
    // TODO possibly move this
    @Contract(pure = true)
    public boolean hasMetRequirements(final Quest quest) {
        for (final String requiredQuestId : quest.getRequirements()) {
            final QuestProgress requiredQuestProgress = this.questProgressMap.get(requiredQuestId);
            if (requiredQuestProgress == null || !requiredQuestProgress.isCompletedBefore()) {
                // if we decide to change the method return type to states like "DOES_NOT_EXIST"
                // or "COMPLETED_BEFORE" we will need to change the quest existance check order
                return false;
            }

            final Quest requiredQuest = this.plugin.getQuestManager().getQuestById(requiredQuestId);
            if (requiredQuest == null) {
                // TODO not sure if we actually need this check however probably
                //      forcing the server owner to fix the quest options instead
                //      of just ignoring the fact it's broken is better?
                return false;
            }
        }

        return true;
    }

    /**
     * @return the associated player UUID
     */
    @Contract(pure = true)
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * Gets the {@link QuestProgress} for a specified {@link Quest}. Generates a new one if it does not exist.
     *
     * @param quest the quest to get the progress for
     * @return {@link QuestProgress} or a blank generated one if the quest does not exist
     */
    public QuestProgress getQuestProgress(final Quest quest) {
        if (DEBUG_ISSUE_760 && !this.plugin.isPrimaryThread()) {
            //noinspection CallToPrintStackTrace
            new IllegalStateException("async getQuestProgress call").printStackTrace();
        }

        final QuestProgress questProgress = this.getQuestProgressOrNull(quest);
        return questProgress != null ? questProgress : this.generateBlankQuestProgress(quest);
    }

    /**
     * Gets the {@link QuestProgress} for a specified {@link Quest}. Returns null if it does not exist.
     *
     * @param quest the quest to get the progress for
     * @return {@link QuestProgress} or null if the quest does not exist
     */
    @Contract(pure = true)
    public @Nullable QuestProgress getQuestProgressOrNull(final Quest quest) {
        return this.questProgressMap.get(quest.getId());
    }

    /**
     * Tests whether the player has a specified {@link Quest} started.
     *
     * @param quest the quest to check for
     * @return true if player has the quest started
     */
    @Contract(pure = true)
    public boolean hasQuestStarted(final Quest quest) {
        final QuestProgress questProgress = this.getQuestProgressOrNull(quest);
        return questProgress != null && questProgress.isStarted();
    }

    /**
     * Generate a new blank {@link QuestProgress} for a specified {@link Quest} with {@link QuestProgress#isModified()} set to {@code false}.
     *
     * @param quest the quest to generate the progress for
     * @return the generated blank {@link QuestProgress}
     */
    public QuestProgress generateBlankQuestProgress(final Quest quest) {
        return this.generateBlankQuestProgress(quest, false);
    }

    /**
     * Generate a new blank {@link QuestProgress} for a specified {@link Quest}.
     *
     * @param quest    the quest to generate the progress for
     * @param modified the modified state of the quest
     * @return the generated blank {@link QuestProgress}
     */
    public QuestProgress generateBlankQuestProgress(final Quest quest, final boolean modified) {
        final QuestProgress questProgress = new QuestProgress(this.plugin, quest.getId(), this.playerUUID, false, 0L, false, false, 0L, modified);

        for (final Task task : quest.getTasks()) {
            final TaskProgress taskProgress = new TaskProgress(questProgress, task.getId(), this.playerUUID, null, false, modified);
            questProgress.addTaskProgress(taskProgress);
        }

        this.addQuestProgress(questProgress);
        return questProgress;
    }

    /**
     * Clears quest progress map.
     */
    public void clear() {
        this.questProgressMap.clear();
    }

    /**
     * Reset quests to their default state. More specifically, this will reset all
     * quest progress with non-default parameters back to default and only
     * set the modified flag in that case.
     */
    public void reset() {
        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            if (!questProgress.hasNonDefaultValues()) {
                continue;
            }

            final Quest quest = this.plugin.getQuestManager().getQuestById(questProgress.getQuestId());
            if (quest == null) {
                continue;
            }

            this.generateBlankQuestProgress(quest, true);
        }
    }

    /**
     * Removes any references to quests or tasks which are no longer defined in the config.
     */
    @Deprecated
    public void clean() {
        this.plugin.getQuestsLogger().debug("Cleaning file " + this.playerUUID + ".");

        if (!this.plugin.getTaskTypeManager().areRegistrationsOpen()) {
            final List<String> invalidQuestIds = new ArrayList<>();

            for (final Map.Entry<String, QuestProgress> questProgressEntry : this.questProgressMap.entrySet()) {
                final String questId = questProgressEntry.getKey();

                final Quest quest = this.plugin.getQuestManager().getQuestById(questId);
                if (quest == null) {
                    invalidQuestIds.add(questId);

                    // tasks will be removed with the quest
                    continue;
                }

                final QuestProgress questProgress = questProgressEntry.getValue();
                final Map<String, TaskProgress> taskProgressMap = questProgress.getTaskProgressMap();
                final List<String> invalidTaskIds = new ArrayList<>();

                for (final String taskId : taskProgressMap.keySet()) {
                    final Task task = quest.getTaskById(taskId);

                    if (task == null) {
                        invalidTaskIds.add(taskId);
                    }
                }

                for (final String taskId : invalidTaskIds) {
                    taskProgressMap.remove(taskId);
                }
            }

            for (final String questId : invalidQuestIds) {
                this.questProgressMap.remove(questId);
            }
        }
    }

    /**
     * @param modified whether the object has been modified and needs to be saved
     */
    public void setModified(final boolean modified) {
        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            questProgress.setModified(modified);
        }
    }

    // DEPRECATED AND FOR REMOVAL

    /**
     * Returns all {@link Quest} a player has encountered (not to be confused with a collection of quest progress).
     *
     * @return list of matching quests
     */
    @Deprecated(forRemoval = true)
    @Contract(pure = true)
    public List<Quest> getAllQuestsFromProgress(final QuestsProgressFilter filter) {
        final List<Quest> quests = new ArrayList<>();

        for (final QuestProgress questProgress : this.questProgressMap.values()) {
            final boolean matches = filter.matches(questProgress);
            if (!matches) {
                continue;
            }

            final Quest quest = this.plugin.getQuestManager().getQuestById(questProgress.getQuestId());
            if (quest == null) {
                continue;
            }

            quests.add(quest);
        }

        return quests;
    }

    @Deprecated(forRemoval = true)
    public enum QuestsProgressFilter {
        ALL("all") {
            @Override
            @Contract(pure = true)
            public boolean matches(final QuestProgress questProgress) {
                return QuestProgressFilter.ALL.matchesProgress(questProgress);
            }
        },
        COMPLETED("completed") {
            @Override
            @Contract(pure = true)
            public boolean matches(final QuestProgress questProgress) {
                return QuestProgressFilter.COMPLETED.matchesProgress(questProgress);
            }
        },
        COMPLETED_BEFORE("completedBefore") {
            @Override
            @Contract(pure = true)
            public boolean matches(final QuestProgress questProgress) {
                return QuestProgressFilter.COMPLETED_BEFORE.matchesProgress(questProgress);
            }
        },
        STARTED("started") {
            @Override
            @Contract(pure = true)
            public boolean matches(final QuestProgress questProgress) {
                return QuestProgressFilter.STARTED.matchesProgress(questProgress);
            }
        };

        private final String legacy;

        QuestsProgressFilter(final String legacy) {
            this.legacy = legacy;
        }

        @SuppressWarnings("unused")
        @Contract(pure = true)
        public String getLegacy() {
            return this.legacy;
        }

        @Contract(pure = true)
        public abstract boolean matches(final QuestProgress questProgress);

        // And some static things to improve legacy performance (is it even used?)

        private static final QuestsProgressFilter[] FILTERS = QuestsProgressFilter.values();

        private static final Map<String, QuestsProgressFilter> legacyToFilterMap = new HashMap<>(QuestsProgressFilter.FILTERS.length) {{
            for (final QuestsProgressFilter questsProgressFilter : QuestsProgressFilter.FILTERS) {
                this.put(questsProgressFilter.legacy, questsProgressFilter);
            }
        }};

        @SuppressWarnings("unused")
        @Contract(pure = true)
        public static QuestsProgressFilter fromLegacy(final String legacy) {
            return QuestsProgressFilter.legacyToFilterMap.getOrDefault(legacy, QuestsProgressFilter.ALL);
        }
    }

    /**
     * It's equivalent to {@code QuestProgressFile#setModified(false)}.
     *
     * @see QuestProgressFile#setModified(boolean)
     */
    @Deprecated(forRemoval = true)
    public void resetModified() {
        this.setModified(false);
    }
}
