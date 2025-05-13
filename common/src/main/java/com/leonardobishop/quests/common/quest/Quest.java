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
    private List<String> cancelString;
    private List<String> expiryString;
    private List<String> startCommands;
    private List<String> cancelCommands;
    private List<String> expiryCommands;
    private String vaultReward;
    private boolean repeatEnabled;
    private boolean cooldownEnabled;
    private int cooldown;
    private boolean timeLimitEnabled;
    private int timeLimit;
    private int sortOrder;
    private boolean permissionRequired;
    private boolean autoStartEnabled;
    private boolean cancellable;
    private boolean countsTowardsLimit;
    private boolean countsTowardsCompleted;
    private boolean hidden;
    private Map<String, String> placeholders;
    private Map<String, String> progressPlaceholders;
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
     * @return permission required to start the quest
     */
    public @Nullable String getPermission() {
        return isPermissionRequired() ? "quests.quest." + id : null;
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
     * Get the cancel string of the quest.
     * The cancel string is a series of messages sent to the player upon cancelling the quest.
     *
     * @return immutable list of messages to send
     */
    public @NotNull List<String> getCancelString() {
        return Collections.unmodifiableList(cancelString);
    }

    /**
     * Get the expiry string of the quest.
     * The expiry string is a series of messages sent to the player upon expiring the quest.
     *
     * @return immutable list of messages to send
     */
    public @NotNull List<String> getExpiryString() {
        return Collections.unmodifiableList(expiryString);
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
     * Get the start commands for this quest.
     * The start commands is a list of commands to be executed upon starting the quest.
     *
     * @return immutable list of commands
     */
    public List<String> getStartCommands() {
        return Collections.unmodifiableList(startCommands);
    }

    /**
     * Get the cancel commands for this quest.
     * The cancel commands is a list of commands to be executed upon cancelling the quest.
     *
     * @return immutable list of commands
     */
    public List<String> getCancelCommands() {
        return Collections.unmodifiableList(cancelCommands);
    }

    /**
     * Get the expiry commands for this quest.
     * The expiry commands is a list of commands to be executed upon expiring the quest.
     *
     * @return immutable list of commands
     */
    public List<String> getExpiryCommands() {
        return Collections.unmodifiableList(expiryCommands);
    }

    /**
     * Get the Vault reward for this quest.
     * The Vault reward is an amount of Vault economy money to be given upon completing the quest.
     *
     * @return string
     */
    public @Nullable String getVaultReward() {
        return this.vaultReward;
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
     * @return the cooldown, in minutes
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Get whether this quest has a time limit.
     *
     * @return boolean
     */
    public boolean isTimeLimitEnabled() {
        return timeLimitEnabled;
    }

    /**
     * Get the time limit for this quest.
     * Whether or not this time limit is in use depends on {@link Quest#isTimeLimitEnabled()}.
     *
     * @return the time limit, in minutes
     */
    public int getTimeLimit() {
        return timeLimit;
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
     * Get the local progress placeholders for this quest, which is not exposed to PlaceholderAPI.
     *
     * @return immutable map of progress placeholders
     */
    public @NotNull Map<String, String> getProgressPlaceholders() {
        return Collections.unmodifiableMap(progressPlaceholders);
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
     * Get if this quest should be cancellable.
     *
     * @return boolean
     */
    public boolean isCancellable() {
        return cancellable;
    }

    /**
     * Get whether this quest should count towards the player's total quest limit.
     *
     * @return boolean
     */
    public boolean doesCountTowardsLimit() {
        return countsTowardsLimit;
    }

    /**
     * Get whether this quest should count towards the player's quests completed.
     *
     * @return boolean
     */
    public boolean doesCountTowardsCompleted() {
        return countsTowardsCompleted;
    }

    /**
     * Get whether this quest should be hidden from menus.
     *
     * @return boolean
     */
    public boolean isHidden() {
        return hidden;
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
        private List<String> cancelString = Collections.emptyList();
        private List<String> expiryString = Collections.emptyList();
        private List<String> startCommands = Collections.emptyList();
        private List<String> cancelCommands = Collections.emptyList();
        private List<String> expiryCommands = Collections.emptyList();
        private String vaultReward = null;
        private boolean repeatEnabled = false;
        private boolean cooldownEnabled = false;
        private int cooldown = 0;
        private boolean timeLimitEnabled = false;
        private int timeLimit = 0;
        private int sortOrder = 1;
        private boolean permissionRequired = false;
        private boolean autoStartEnabled = false;
        private boolean cancellable = true;
        private boolean countsTowardsLimit = true;
        private boolean countsTowardsCompleted = true;
        private boolean hidden = false;
        private Map<String, String> placeholders = Collections.emptyMap();
        private Map<String, String> progressPlaceholders = Collections.emptyMap();
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

        public Builder withCancelString(List<String> cancelString) {
            this.cancelString = cancelString;
            return this;
        }

        public Builder withExpiryString(List<String> expiryString) {
            this.expiryString = expiryString;
            return this;
        }

        public Builder withStartCommands(List<String> startCommands) {
            this.startCommands = startCommands;
            return this;
        }

        public Builder withCancelCommands(List<String> cancelCommands) {
            this.cancelCommands = cancelCommands;
            return this;
        }

        public Builder withExpiryCommands(List<String> expiryCommands) {
            this.expiryCommands = expiryCommands;
            return this;
        }

        public Builder withVaultReward(String vaultReward) {
            this.vaultReward = vaultReward;
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

        public Builder withTimeLimit(int timeLimit) {
            this.timeLimit = timeLimit;
            return this;
        }

        public Builder withPlaceholders(Map<String, String> placeholders) {
            this.placeholders = placeholders;
            return this;
        }

        public Builder withProgressPlaceholders(Map<String, String> progressPlaceholders) {
            this.progressPlaceholders = progressPlaceholders;
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

        public Builder withTimeLimitEnabled(boolean timeLimitEnabled) {
            this.timeLimitEnabled = timeLimitEnabled;
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

        public Builder withCancellable(boolean cancellable) {
            this.cancellable = cancellable;
            return this;
        }

        public Builder withCountsTowardsLimit(boolean countsTowardsLimit) {
            this.countsTowardsLimit = countsTowardsLimit;
            return this;
        }

        public Builder withCountsTowardsCompleted(boolean countsTowardsCompleted) {
            this.countsTowardsCompleted = countsTowardsCompleted;
            return this;
        }

        public Builder withHidden(boolean hidden) {
            this.hidden = hidden;
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
            quest.cancelString = this.cancelString;
            quest.expiryString = this.expiryString;
            quest.startCommands = this.startCommands;
            quest.cancelCommands = this.cancelCommands;
            quest.expiryCommands = this.expiryCommands;
            quest.vaultReward = this.vaultReward;
            quest.repeatEnabled = this.repeatEnabled;
            quest.cooldownEnabled = this.cooldownEnabled;
            quest.cooldown = this.cooldown;
            quest.timeLimitEnabled = this.timeLimitEnabled;
            quest.timeLimit = this.timeLimit;
            quest.sortOrder = this.sortOrder;
            quest.permissionRequired = this.permissionRequired;
            quest.autoStartEnabled = this.autoStartEnabled;
            quest.countsTowardsLimit = this.countsTowardsLimit;
            quest.countsTowardsCompleted = this.countsTowardsCompleted;
            quest.hidden = this.hidden;
            quest.cancellable = this.cancellable;
            quest.placeholders = this.placeholders;
            quest.progressPlaceholders = this.progressPlaceholders;
            quest.categoryid = this.categoryid;
            return quest;
        }
    }
}
