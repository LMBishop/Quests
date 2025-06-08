package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a category for organizing quests within the quest system. Each category can have
 * associated quests, and it can specify whether permission is required to access it and whether
 * it should be hidden from the plugin menus.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
public final class Category {

    private static final String PERMISSION_PREFIX = "quests.category.";

    private final String id;
    private final @Nullable String guiName;
    private final List<String> registeredQuestIds;
    private final boolean permissionRequired;
    private final boolean hidden;

    /**
     * Constructs a Category with the specified parameters.
     *
     * @param id                 the unique identifier for the category; must not be null
     * @param guiName            the custom GUI title to be shown in this category quest menu
     * @param permissionRequired whether a permission is required to access this category
     * @param hidden             whether the category should be hidden from view
     */
    public Category(final String id, final @Nullable String guiName, final boolean permissionRequired, final boolean hidden) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.guiName = guiName;
        this.registeredQuestIds = new ArrayList<>();
        this.permissionRequired = permissionRequired;
        this.hidden = hidden;
    }

    /**
     * Constructs a Category with no custom {@link Category#guiName}.
     *
     * @param id                 the unique identifier for the category; must not be null
     * @param permissionRequired whether a permission is required to access this category
     * @param hidden             whether the category should be hidden from view
     */
    public Category(final String id, final boolean permissionRequired, final boolean hidden) {
        this(id, null, permissionRequired, hidden);
    }

    /**
     * Constructs a Category with {@link Category#hidden} set to {@code false}.
     *
     * @param id                 the unique identifier for the category; must not be null
     * @param guiName            the custom GUI title to be shown in this category quest menu
     * @param permissionRequired whether a permission is required to access this category
     */
    public Category(final String id, final @Nullable String guiName, final boolean permissionRequired) {
        this(id, guiName, permissionRequired, false);
    }

    /**
     * Constructs a Category with no custom {@link Category#guiName} and {@link Category#hidden} set to {@code false}.
     *
     * @param id                 the unique identifier for the category; must not be null
     * @param permissionRequired whether a permission is required to access this category
     */
    public Category(final String id, final boolean permissionRequired) {
        this(id, null, permissionRequired);
    }

    /**
     * Returns the unique identifier of this category.
     *
     * @return the category ID
     */
    @Contract(pure = true)
    public String getId() {
        return this.id;
    }

    /**
     * Returns the custom GUI title for this category.
     *
     * @return the category custom GUI title
     */
    public @Nullable String getGUIName() {
        return this.guiName;
    }

    /**
     * Checks if a specific permission is required to access this category and start quests within it.
     * The permission will be in the form of "quests.category.[category id]".
     *
     * @return true if permission is required, false otherwise
     * @see Category#getPermission() Permission getter
     */
    @Contract(pure = true)
    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    /**
     * Returns the permission required to start quests in this category.
     *
     * @return the permission string if required, or null if no permission is needed
     */
    @Contract(pure = true)
    public @Nullable String getPermission() {
        return this.permissionRequired ? PERMISSION_PREFIX + this.id : null;
    }

    /**
     * Registers a new quest ID to this category.
     *
     * @param questId the quest ID to register; must not be null
     */
    public void registerQuestId(final String questId) {
        Objects.requireNonNull(questId, "questId cannot be null");

        this.registeredQuestIds.add(questId);
    }

    /**
     * Returns an unmodifiable list of quest IDs that are registered to this category.
     *
     * @return an unmodifiable list of registered quest IDs
     */
    @Contract(pure = true)
    public @UnmodifiableView List<String> getRegisteredQuestIds() {
        return Collections.unmodifiableList(this.registeredQuestIds);
    }

    /**
     * Checks if this category is hidden from view.
     *
     * @return true if the category is hidden, false otherwise
     */
    @Contract(pure = true)
    public boolean isHidden() {
        return this.hidden;
    }
}
