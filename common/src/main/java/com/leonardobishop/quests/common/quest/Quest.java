package com.leonardobishop.quests.common.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest implements Comparable<Quest> {

    private final Map<String, Task> tasks = new HashMap<>();
    private String id;
    private List<String> rewards;
    private List<String> requirements;
    private List<String> rewardString;
    private List<String> startString;
    private boolean repeatEnabled;
    private boolean cooldownEnabled;
    private int cooldown;
    private int sortOrder;
    private boolean permissionRequired;
    private boolean autoStartEnabled;
    private Map<String, String> placeholders;
    private String categoryid;

    private Quest() { }

    public void registerTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }

    public List<Task> getTasksOfType(String type) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : getTasks()) {
            if (task.getType().equals(type)) {
                tasks.add(task);
            }
        }
        return tasks;
    }


    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public List<String> getRewardString() {
        return rewardString;
    }

    public List<String> getStartString() {
        return startString;
    }

    public String getId() {
        return id;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public boolean isRepeatable() {
        return repeatEnabled;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public int getCooldown() {
        return cooldown;
    }

    public String getCategoryId() {
        return categoryid;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isAutoStartEnabled() {
        return autoStartEnabled;
    }

    @Override
    public int compareTo(Quest quest) {
        return (sortOrder - quest.sortOrder);
    }

    public static class Builder {

        private final String id;
        private List<String> rewards = Collections.emptyList();
        private List<String> requirements = Collections.emptyList();
        private List<String> rewardString = Collections.emptyList();
        private List<String> startString = Collections.emptyList();
        private boolean repeatEnabled = false;
        private boolean cooldownEnabled = false;
        private int cooldown = 0;
        private int sortOrder = 1;
        private boolean permissionRequired = false;
        private boolean autoStartEnabled = false;
        private Map<String, String> placeholders = Collections.emptyMap();
        private String categoryid = null;

        public Builder(String id) {
            this.id = id;
        }

        public Builder withRewards(List<String> rewards) {
            this.rewards = rewards;
            return this;
        }

        public Builder withRequirements(List<String> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder withRewardString(List<String> rewardString) {
            this.rewardString = rewardString;
            return this;
        }

        public Builder withStartString(List<String> startString) {
            this.startString = startString;
            return this;
        }

        public Builder withSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder withCooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder withPlaceholders(Map<String, String> placeholders) {
            this.placeholders = placeholders;
            return this;
        }

        public Builder withRepeatEnabled(boolean repeatEnabled) {
            this.repeatEnabled = repeatEnabled;
            return this;
        }

        public Builder withCooldownEnabled(boolean cooldownEnabled) {
            this.cooldownEnabled = cooldownEnabled;
            return this;
        }

        public Builder withPermissionRequired(boolean permissionRequired) {
            this.permissionRequired = permissionRequired;
            return this;
        }

        public Builder withAutoStartEnabled(boolean autoStartEnabled) {
            this.autoStartEnabled = autoStartEnabled;
            return this;
        }

        public Builder inCategory(String categoryid) {
            this.categoryid = categoryid;
            return this;
        }

        public Quest build() {
            Quest quest = new Quest();
            quest.id = this.id;
            quest.rewards = this.rewards;
            quest.requirements = this.requirements;
            quest.rewardString = this.rewardString;
            quest.startString = this.startString;
            quest.repeatEnabled = this.repeatEnabled;
            quest.cooldownEnabled = this.cooldownEnabled;
            quest.cooldown = this.cooldown;
            quest.sortOrder = this.sortOrder;
            quest.permissionRequired = this.permissionRequired;
            quest.autoStartEnabled = this.autoStartEnabled;
            quest.placeholders = this.placeholders;
            quest.categoryid = this.categoryid;

            return quest;
        }

    }
}
