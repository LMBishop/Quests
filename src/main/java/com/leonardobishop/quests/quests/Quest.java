package com.leonardobishop.quests.quests;

import com.leonardobishop.quests.menu.QItemStack;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest implements Comparable<Quest> {

    private Map<String, Task> tasks = new HashMap<>();
    private final String id;
    private final QItemStack displayItem;
    private final List<String> rewards;
    private final List<String> requirements;
    private final List<String> rewardString;
    private final List<String> startString;
    private final boolean repeatable;
    private final boolean cooldownEnabled;
    private final int cooldown;
    private final int sortOrder;
    private final boolean permissionRequired;
    private final Map<String, String> placeholders;
    private String categoryid;


    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, boolean permissionRequired, List<String> rewardString, List<String> startString, Map<String, String> placeholders, String categoryid, int sortOrder) {
        this(id, displayItem, rewards, requirements, repeatable, cooldownEnabled, cooldown, permissionRequired, rewardString, startString, placeholders, sortOrder);
        this.categoryid = categoryid;
    }

    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, boolean permissionRequired, List<String> rewardString, List<String> startString, Map<String, String> placeholders, int sortOrder) {
        this.id = id;
        this.displayItem = displayItem;
        this.rewards = rewards;
        this.requirements = requirements;
        this.repeatable = repeatable;
        this.cooldownEnabled = cooldownEnabled;
        this.cooldown = cooldown;
        this.permissionRequired = permissionRequired;
        this.rewardString = rewardString;
        this.startString = startString;
        this.placeholders = placeholders;
        this.sortOrder = sortOrder;
    }

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

    public QItemStack getDisplayItem() {
        return displayItem;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public boolean isRepeatable() {
        return repeatable;
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

    public String getDisplayNameStripped() {
        return ChatColor.stripColor(this.displayItem.getName());
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public int compareTo(Quest quest) {
        return (sortOrder - quest.sortOrder);
    }
}
