package com.leonardobishop.quests.player.questprogressfile;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
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

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.modified = true;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
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
        TaskProgress tP = taskProgress.getOrDefault(taskId, null);
        if (tP == null) {
            repairTaskProgress(taskId);
            tP = taskProgress.getOrDefault(taskId, null);
        }
        return tP;
    }

    public void repairTaskProgress(String taskid) {
        TaskProgress taskProgress = new TaskProgress(taskid, null, player, false, false);
        this.addTaskProgress(taskProgress);
    }

    public boolean isWorthSaving() {
        if (modified) return true;
        else {
            for (TaskProgress progress : this.taskProgress.values()) {
                if (progress.isModified()) return true;
            }
            return false;
        }
    }

    public void resetModified() {
        this.modified = false;
        for (TaskProgress progress : this.taskProgress.values()) {
            progress.setModified(false);
        }
    }
}
