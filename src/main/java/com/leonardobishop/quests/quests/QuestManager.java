package com.leonardobishop.quests.quests;

import com.leonardobishop.quests.Quests;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestManager {

    private final Quests plugin;

    public QuestManager(Quests plugin) {
        this.plugin = plugin;
    }

    private Map<String, Quest> quests = new LinkedHashMap<>();
    private List<Category> categories = new ArrayList<>();

    public void registerQuest(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    public Quest getQuestById(String id) {
        return quests.get(id);
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public void registerCategory(Category category) { categories.add(category); }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategoryById(String id) {
        for (Category category : categories) {
            if (category.getId().equals(id)) return category;
        }
        return null;
    }

    public Quests getPlugin() {
        return this.plugin;
    }
}
