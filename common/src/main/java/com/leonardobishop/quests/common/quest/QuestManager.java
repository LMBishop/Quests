package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.plugin.Quests;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The quests manager stores all present Quests and Categories on the server and is used as a registry.
 */
public class QuestManager {

    private final Quests plugin;
    private final Map<String, Quest> quests = new LinkedHashMap<>();
    private final List<Category> categories = new ArrayList<>();

    public QuestManager(Quests plugin) {
        this.plugin = plugin;
    }

    /**
     * Register a quest with the quest manager
     *
     * @param quest the category to register
     */
    public void registerQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        quests.put(quest.getId(), quest);
    }

    /**
     * @param id id to match
     * @return {@link Quest}, or null
     */
    public @Nullable Quest getQuestById(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");

        return quests.get(id);
    }

    /**
     * Get a map of id-quest of all quests registered
     * @return immutable map of all {@link Quest}
     */
    public @NotNull Map<String, Quest> getQuests() {
        return Collections.unmodifiableMap(quests);
    }

    /**
     * Register a category with the quest manager
     *
     * @param category the category to register
     */
    public void registerCategory(@NotNull Category category) {
        Objects.requireNonNull(category, "category cannot be null");

        categories.add(category);
    }

    /**
     * @return immutable list of all {@link Category}
     */
    public @NotNull List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Get a specific category by id
     *
     * @param id the id
     * @return {@link Category}, or null
     */
    public @Nullable Category getCategoryById(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");

        for (Category category : categories) {
            if (category.getId().equals(id)) return category;
        }
        return null;
    }

    /**
     * Reset the quest manager and clears all registered quests and categories
     */
    public void clear() {
        quests.clear();
        categories.clear();
    }

}
