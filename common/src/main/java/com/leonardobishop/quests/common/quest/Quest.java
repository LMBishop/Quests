package com.leonardobishop.quests.common.quest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Quest implements Comparable<Quest> {

    private final Map<String, Task> tasks = new HashMap<>();
    private final Map<String, List<Task>> tasksByType = new HashMap<>();
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

    /**
     * Register a task to this quest.
     *
     * @param task the task to register
     */
    public void registerTask(@NotNull Task task) {
        Objects.requireNonNull(task, "task cannot be null");

        tasks.put(task.getId(), task);
        tasksByType.compute(task.getType(), (type, list) -> {
            if (list == null) {
                return new ArrayList<>(Collections.singletonList(task));
            } else {
                list.add(task);
                return list;
            }
        });
    }

    /**
     * Get all tasks registered to this quest.
     *
     * @return immutable list containing all {@link Task}
     */
    public @NotNull Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks.values());
    }

    /**
     * Get a specific task registered to this quest.
     *
     * @param id task id
     * @return {@link Task}, or null if not exists
     */
    public @Nullable Task getTaskById(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");

        return tasks.get(id);
    }

    /**
     * Get a list of all task of a specific task type.
     *
     * @param type the task type
     * @return list containing all tasks of type
     */
    public @NotNull List<Task> getTasksOfType(String type) {
        Objects.requireNonNull(type, "type cannot be null");

        List<Task> list = tasksByType.get(type);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }


    /**
     * Get if a specific permission is required to start this quest.
     * This permission will be in the form of "quests.quest.[quest id]".
     *
     * @return boolean
     */
    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    /**
     * Get the reward string of the quest.
     * The reward string is a series of messages sent to the player upon completing the quest.
     *
     * @return immutable list of messages to send
     */
    public @NotNull List<String> getRewardString() {
        return Collections.unmodifiableList(rewardString);
    }

    /**
     * Get the start string of the quest.
     * The start string is a series of messages sent to the player upon starting the quest.
     *
     * @return immutable list of messages to send
     */
    public @NotNull List<String> getStartString() {
        return Collections.unmodifiableList(startString);
    }

    /**
     * Get the id of this quest.
     *
     * @return id
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the rewards for this quest.
     * The rewards is a list of commands to be executed upon completing the quest.
     *
     * @return immutable list of rewards
     */
    public @NotNull List<String> getRewards() {
        return Collections.unmodifiableList(rewards);
    }

    /**
     * Get the requirements for this quest.
     * The requirements is a list of quests ids the player must have completed at least once to start this quest.
     * The quest ids may or may not represent actual quests and are only validated by the plugin with a warning.
     *
     * @return immutable list of quest requirements
     */
    public @NotNull List<String> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    /**
     * Get if this quest can be repeated after completion.
     *
     * @return boolean
     */
    public boolean isRepeatable() {
        return repeatEnabled;
    }

    /**
     * Get if this quest has a cooldown enabled after completion.
     * Whether or not the quest enters a cooldown phase for the player depends
     * on if it is repeatable in the first place: {@link Quest#isRepeatable()}
     *
     * @return boolean
     */
    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    /**
     * Get the cooldown for this quest between completing and restarting the quest.
     * Whether or not this cooldown is in use depends on {@link Quest#isCooldownEnabled()}.
     *
     * @return the cooldown, in seconds
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Get the category id this quest is in.
     *
     * @return the category id, or null
     */
    public @Nullable String getCategoryId() {
        return categoryid;
    }

    /**
     * Get the local placeholders for this quest, which is exposed to PlaceholderAPI.
     *
     * @return immutable map of placeholders
     */
    public @NotNull Map<String, String> getPlaceholders() {
        return Collections.unmodifiableMap(placeholders);
    }

    /**
     * Get the sort order for this quest in the GUI.
     * Numbers closer to Integer.MIN_VALUE have greater priority.
     *
     * @return any integer, both negative or positive
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Get if quest-specific autostart is enabled for this quest.
     *
     * @return boolean
     */
    public boolean isAutoStartEnabled() {
        return autoStartEnabled;
    }

    /**
     * Compare the sort orders for this quest with another quest.
     *
     * @see Comparable#compareTo(Object)
     * @param quest the quest to compare with
     * @return a negative integer, zero, or a positive integer
     */
    @Override
    public int compareTo(@NotNull Quest quest) {
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
