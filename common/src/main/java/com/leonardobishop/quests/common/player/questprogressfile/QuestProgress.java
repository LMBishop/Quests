package com.leonardobishop.quests.common.player.questprogressfile;

import com.leonardobishop.quests.common.plugin.Quests;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class QuestProgress {

    private final Quests plugin;
    private final String questId;
    private final UUID playerUUID;
    private final Map<String, TaskProgress> taskProgressMap;

    private boolean started;
    private long startedDate;
    private boolean completed;
    private boolean completedBefore;
    private long completionDate;
    private boolean modified;

    /**
     * Constructs a QuestProgress.
     *
     * @param plugin          the plugin instance
     * @param questId         the associated quest ID
     * @param playerUUID      the associated player UUID
     * @param started         whether the quest is started
     * @param startedDate     the date of the last quest start
     * @param completed       whether the quest is completed
     * @param completedBefore whether the quest has been completed before
     * @param completionDate  the date of the last quest completion
     * @param modified        whether the object has been modified and needs to be saved
     */
    public QuestProgress(final @NotNull Quests plugin, final @NotNull String questId, final @NotNull UUID playerUUID, final boolean started, final long startedDate, final boolean completed, final boolean completedBefore, final long completionDate, final boolean modified) {
        this.plugin = plugin;
        this.questId = questId;
        this.playerUUID = playerUUID;
        this.taskProgressMap = new HashMap<>();
        this.started = started;
        this.startedDate = startedDate;
        this.completed = completed;
        this.completedBefore = completedBefore;
        this.completionDate = completionDate;
        this.modified = modified;
    }

    /**
     * Constructs a QuestProgress with {@link QuestProgress#modified} set to {@code false}.
     *
     * @param plugin          the plugin instance
     * @param questId         the associated quest ID
     * @param playerUUID      the associated player UUID
     * @param started         whether the quest is started
     * @param startedDate     the date of the last quest start
     * @param completed       whether the quest is completed
     * @param completedBefore whether the quest has been completed before
     * @param completionDate  the date of the last quest completion
     */
    public QuestProgress(final @NotNull Quests plugin, final @NotNull String questId, final @NotNull UUID playerUUID, final boolean started, final long startedDate, final boolean completed, final boolean completedBefore, final long completionDate) {
        this(plugin, questId, playerUUID, started, startedDate, completed, completedBefore, completionDate, false);
    }

    /**
     * Constructs a data-only clone from a QuestProgress instance.
     *
     * @param questProgress the quest progress instance
     */
    @ApiStatus.Internal
    public QuestProgress(final @NotNull QuestProgress questProgress) {
        final Set<Map.Entry<String, TaskProgress>> progressEntries = questProgress.taskProgressMap.entrySet();

        this.plugin = questProgress.plugin;
        this.questId = questProgress.questId;
        this.playerUUID = questProgress.playerUUID;
        this.taskProgressMap = new HashMap<>(progressEntries.size());

        for (final Map.Entry<String, TaskProgress> progressEntry : progressEntries) {
            this.taskProgressMap.put(progressEntry.getKey(), new TaskProgress(progressEntry.getValue()));
        }

        this.started = questProgress.started;
        this.startedDate = questProgress.startedDate;
        this.completed = questProgress.completed;
        this.completedBefore = questProgress.completedBefore;
        this.completionDate = questProgress.completionDate;
        this.modified = questProgress.modified;
    }

    /**
     * @return the associated quest ID
     */
    @Contract(pure = true)
    public @NotNull String getQuestId() {
        return this.questId;
    }

    /**
     * @return the associated player ID
     * @see QuestProgress#getPlayerUUID()
     */
    @Deprecated(forRemoval = true)
    @Contract(pure = true)
    public @NotNull UUID getPlayer() {
        return this.playerUUID;
    }

    /**
     * @return the associated player ID
     */
    @Contract(pure = true)
    public @NotNull UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * @return mutable task progress map
     */
    @Contract(pure = true)
    public @NotNull Map<String, TaskProgress> getTaskProgressMap() {
        return this.taskProgressMap;
    }

    /**
     * @return mutable task progress map values collection
     * @see QuestProgress#getTaskProgress()
     */
    @Deprecated(forRemoval = true)
    @Contract(pure = true)
    public @NotNull Collection<TaskProgress> getTaskProgress() {
        return this.taskProgressMap.values();
    }

    /**
     * @return mutable task progress map values collection
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public @NotNull Collection<TaskProgress> getAllTaskProgress() {
        return this.taskProgressMap.values();
    }

    /**
     * Gets the {@link TaskProgress} for a specified task ID. Generates a new one if it does not exist.
     *
     * @param taskId the task ID to get the progress for
     * @return {@link TaskProgress} or a blank generated one if the task does not exist
     */
    public @NotNull TaskProgress getTaskProgress(final @NotNull String taskId) {
        final TaskProgress taskProgress = this.taskProgressMap.get(taskId);
        if (taskProgress != null) {
            return taskProgress;
        }

        final TaskProgress newTaskProgress = new TaskProgress(this, taskId, this.playerUUID, null, false, false);
        this.addTaskProgress(newTaskProgress);
        return newTaskProgress;
    }

    /**
     * Gets the {@link TaskProgress} for a specified task ID. Returns null if it does not exist.
     *
     * @param taskId the task ID to get the progress for
     * @return {@link TaskProgress} or null if the task does not exist
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public @Nullable TaskProgress getTaskProgressOrNull(final @NotNull String taskId) {
        return this.taskProgressMap.get(taskId);
    }

    /**
     * @param taskId the task ID to repair the progress for
     */
    @Deprecated(forRemoval = true)
    @ApiStatus.Internal
    public void repairTaskProgress(final @NotNull String taskId) {
        final TaskProgress taskProgress = new TaskProgress(this, taskId, this.playerUUID, null, false, false);
        this.addTaskProgress(taskProgress);
    }

    /**
     * @param taskProgress the task progress to put into the task progress map
     */
    public void addTaskProgress(final @NotNull TaskProgress taskProgress) {
        this.taskProgressMap.put(taskProgress.getTaskId(), taskProgress);
    }

    /**
     * @return whether the quest is started
     */
    @Contract(pure = true)
    public boolean isStarted() {
        return this.started;
    }

    /**
     * @param started whether the quest is started
     */
    public void setStarted(final boolean started) {
        this.started = started;
        this.modified = true;
    }

    /**
     * @return the date of the last quest start
     */
    @Contract(pure = true)
    public long getStartedDate() {
        return this.startedDate;
    }

    /**
     * @param startedDate the date of the last quest start
     */
    public void setStartedDate(final long startedDate) {
        this.startedDate = startedDate;
        this.modified = true;
    }

    /**
     * @return whether the quest is completed
     */
    @Contract(pure = true)
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * @param completed whether the quest is completed
     */
    public void setCompleted(final boolean completed) {
        this.completed = completed;
        this.modified = true;
    }

    /**
     * @return whether the quest has been completed before
     */
    @Contract(pure = true)
    public boolean isCompletedBefore() {
        return this.completedBefore;
    }

    /**
     * @param completedBefore whether the quest has been completed before
     */
    public void setCompletedBefore(final boolean completedBefore) {
        this.completedBefore = completedBefore;
        this.modified = true;
    }

    /**
     * @return the date of the last quest completion
     */
    @Contract(pure = true)
    public long getCompletionDate() {
        return this.completionDate;
    }

    /**
     * @param completionDate the date of the last quest completion
     */
    public void setCompletionDate(final long completionDate) {
        this.completionDate = completionDate;
        this.modified = true;
    }

    /**
     * @return whether the object has been modified and needs to be saved
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public boolean isModified() {
        if (this.modified) {
            return true;
        }

        for (final TaskProgress taskProgress : this.taskProgressMap.values()) {
            if (taskProgress.isModified()) {
                return true;
            }
        }

        return false;
    }

    /**
     * It's equivalent to {@code QuestProgress#setModified(false)}.
     *
     * @see QuestProgress#setModified(boolean)
     */
    @Deprecated(forRemoval = true)
    public void resetModified() {
        this.setModified(false);
    }

    /**
     * @param modified whether the object has been modified and needs to be saved
     */
    public void setModified(final boolean modified) {
        this.modified = modified;

        for (final TaskProgress taskProgress : this.taskProgressMap.values()) {
            taskProgress.setModified(modified);
        }
    }

    /**
     * Gets whether the object has non default values.
     *
     * <p>
     * Fields checked are:<br>
     * - {@link QuestProgress#started}<br>
     * - {@link QuestProgress#startedDate}<br>
     * - {@link QuestProgress#completed}<br>
     * - {@link QuestProgress#completedBefore}<br>
     * - {@link QuestProgress#completionDate}
     * </p>
     *
     * @return whether the object has non default values
     */
    @Contract(pure = true)
    public boolean hasNonDefaultValues() {
        if (this.started || this.startedDate != 0L || this.completed || this.completedBefore || this.completionDate != 0L) {
            return true;
        }

        for (final TaskProgress taskProgress : this.taskProgressMap.values()) {
            if (taskProgress.getProgress() != null || taskProgress.isCompleted()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Queues the {@link QuestProgress} instance for a completion test.
     */
    @SuppressWarnings("unused")
    public void queueForCompletionTest() {
        this.plugin.getQuestCompleter().queueSingular(this);
    }
}
