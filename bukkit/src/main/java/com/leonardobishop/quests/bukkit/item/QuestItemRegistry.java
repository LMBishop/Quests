package com.leonardobishop.quests.bukkit.item;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Used to store itemstacks which may be otherwise configured in specific
 * task types.
 */
public class QuestItemRegistry {

    private final Map<String, QuestItem> registry = new HashMap<>();

    public void registerItem(@NotNull String id, @NotNull QuestItem item) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(item, "item cannot be null");

        if (item.getId() == null) {
            throw new IllegalArgumentException("null id cannot be registered");
        }

        registry.put(id, item);
    }

    public QuestItem getItem(String id) {
        return registry.get(id);
    }

    public void clearRegistry() {
        registry.clear();
    }

    public Collection<QuestItem> getAllItems() {
        return Collections.unmodifiableCollection(registry.values());
    }

}
