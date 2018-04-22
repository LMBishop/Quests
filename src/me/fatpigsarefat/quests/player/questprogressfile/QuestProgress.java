package me.fatpigsarefat.quests.player.questprogressfile;

import java.util.*;

public class QuestProgress {

    private Map<String, TaskProgress> taskProgress = new HashMap<>();
    private String questid;
    private boolean started;
    private boolean completed;
    private boolean completedBefore;
    private long completionDate;
    private UUID player;
    private boolean modified;

    public QuestProgress(String questid, boolean completed, boolean completedBefore, long completionDate, UUID player, boolean started) {
        this.questid = questid;
        this.completed = completed;
        this.completedBefore = completedBefore;
        this.completionDate = completionDate;
        this.player = player;
        this.started = started;
    }

    public QuestProgress(String questid, boolean completed, boolean completedBefore, long completionDate, UUID player, boolean started, boolean modified) {
        this(questid, completed, completedBefore, completionDate, player, started);
        this.modified = modified;
    }

    public String getQuestId() {
        return questid;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setStarted(boolean started) {
        this.started = started;
        this.modified = true;
    }

    public boolean isStarted() {
        return started;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.modified = true;
    }

    public long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(long completionDate) {
        this.completionDate = completionDate;
        this.modified = true;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isCompletedBefore() {
        return completedBefore;
    }

    public void setCompletedBefore(boolean completedBefore) {
        this.completedBefore = completedBefore;
        this.modified = true;
    }

    public void addTaskProgress(TaskProgress taskProgress) {
        this.taskProgress.put(taskProgress.getTaskId(), taskProgress);
    }

    public Collection<TaskProgress> getTaskProgress() {
        return taskProgress.values();
    }

    public TaskProgress getTaskProgress(String taskId) {
        return taskProgress.getOrDefault(taskId, null);
    }

    public boolean isWorthSaving() {
        return modified;
    }
}
