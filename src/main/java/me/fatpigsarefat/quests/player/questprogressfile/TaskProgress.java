package me.fatpigsarefat.quests.player.questprogressfile;

import java.util.UUID;

public class TaskProgress {

    private String taskid;
    private Object progress;
    private UUID player;
    private boolean completed;

    public TaskProgress(String taskid, Object progress, UUID player, boolean completed) {
        this.taskid = taskid;
        this.progress = progress;
        this.completed = completed;
    }

    public String getTaskId() {
        return taskid;
    }

    public Object getProgress() {
        return progress;
    }

    public void setProgress(Object progress) {
        this.progress = progress;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean complete) {
        this.completed = complete;
    }
}
