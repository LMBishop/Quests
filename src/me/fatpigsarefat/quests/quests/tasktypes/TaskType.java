package me.fatpigsarefat.quests.quests.tasktypes;

import me.fatpigsarefat.quests.quests.Quest;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskType implements Listener {

    private List<Quest> quests = new ArrayList<>();
    private String type;
    private String author;
    private String description;

    public TaskType(String type, String author, String description) {
        this.type = type;
        this.author = author;
        this.description = description;
    }

    public TaskType(String type) {
        this.type = type;
    }

    public final void registerQuest(Quest quest) {
        if (!quests.contains(quest)) {
            quests.add(quest);
        }
    }

    public final void unregisterAll() {
        quests.clear();
    }

    public final List<Quest> getRegisteredQuests() {
        return quests;
    }


    public final String getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }
}
