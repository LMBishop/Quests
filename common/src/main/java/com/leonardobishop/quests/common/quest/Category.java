package com.leonardobishop.quests.common.quest;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Category {

    private final String id;
    private final boolean permissionRequired;
    private final List<String> registeredQuestIds = new ArrayList<>();

    public Category(String id, boolean permissionRequired) {
        this.id = id;
        this.permissionRequired = permissionRequired;
    }

    /**
     * Get the id of this category.
     *
     * @return id
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get if a specific permission is required to open this category and start quests within it.
     * This permission will be in the form of "quests.category.[category id]".
     *
     * @return boolean
     */
    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    /**
     * Register a new quest ID to this category
     *
     * @param questId quest id to register
     */
    public void registerQuestId(@NotNull String questId) {
        Objects.requireNonNull(questId, "questId cannot be null");
        registeredQuestIds.add(questId);
    }

    /**
     * Get quest IDs which are registered to this category
     *
     * @return immutable list of quest ids
     */
    public @NotNull List<String> getRegisteredQuestIds() {
        return Collections.unmodifiableList(registeredQuestIds);
    }

}
