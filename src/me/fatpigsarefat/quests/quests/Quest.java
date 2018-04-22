package me.fatpigsarefat.quests.quests;

import me.fatpigsarefat.quests.obj.misc.QItemStack;
import org.bukkit.ChatColor;

import java.util.*;

public class Quest {

    private Map<String, Task> tasks = new HashMap<>();
    //TODO: maybe store by <tasktypename (string), list<task>> since we never get task by id, but always get tasks by type.
    private String id;
    private QItemStack displayItem;
    private List<String> rewards;
    private List<String> requirements;
    private List<String> rewardString;
    private boolean repeatable;
    private boolean cooldownEnabled;
    private int cooldown;
    private String categoryid;


    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, List<String> rewardString, String categoryid) {
        this(id, displayItem, rewards, requirements, repeatable, cooldownEnabled, cooldown, rewardString);
        this.categoryid = categoryid;
    }

    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, List<String> rewardString) {
        this.id = id;
        this.displayItem = displayItem;
        this.rewards = rewards;
        this.requirements = requirements;
        this.repeatable = repeatable;
        this.cooldownEnabled = cooldownEnabled;
        this.cooldown = cooldown;
        this.rewardString = rewardString;
    }

    public void registerTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Collection<Task> getTasks() {
        return tasks.values();
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

    public List<String> getRewardString() {
        return rewardString;
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
}
