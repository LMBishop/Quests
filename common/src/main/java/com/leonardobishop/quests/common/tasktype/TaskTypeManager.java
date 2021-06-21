package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;

import java.util.ArrayList;

/**
 * The task type manager stores all registered task types and registers individual quests to each task type.
 * Task types can only be registered if registrations are enabled, which is typically only during start-up.
 * This is to ensure quests are only registered to task types when all task types have been registered first.
 */
public abstract class TaskTypeManager {

    private final ArrayList<TaskType> taskTypes = new ArrayList<>();
    private final Quests plugin;
    private boolean allowRegistrations;

    public TaskTypeManager(Quests plugin) {
        this.plugin = plugin;
        allowRegistrations = true;
    }

    public void closeRegistrations() {
        allowRegistrations = false;
    }

    public boolean areRegistrationsAccepted() {
        return allowRegistrations;
    }

    public ArrayList<TaskType> getTaskTypes() {
        return taskTypes;
    }

    public void resetTaskTypes() {
        for (TaskType taskType : taskTypes) {
            taskType.getRegisteredQuests().clear();
        }
    }

    public void registerTaskType(TaskType taskType) {
        if (!allowRegistrations) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }
        plugin.getQuestsLogger().info("Task type " + taskType.getType() + " has been registered.");
        taskTypes.add(taskType);
    }

    public void registerQuestTasksWithTaskTypes(Quest quest) {
        if (allowRegistrations) {
            throw new IllegalStateException("Still accepting new task types (type registrations must be closed before registering quests)");
        }
        for (Task task : quest.getTasks()) {
            TaskType t;
            if ((t = getTaskType(task.getType())) != null) {
                t.registerQuest(quest);
            }
        }
    }

    public TaskType getTaskType(String string) {
        for (TaskType taskType : taskTypes) {
            if (taskType.getType().equalsIgnoreCase(string)) {
                return taskType;
            }
        }
        return null;
    }
}
