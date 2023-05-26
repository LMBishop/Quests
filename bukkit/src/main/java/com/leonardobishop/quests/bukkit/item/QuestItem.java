package com.leonardobishop.quests.bukkit.item;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a single quest item.
 */
public abstract class QuestItem {

    private final String type;
    private final String id;

    public QuestItem(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isTemporary() {
        return type == null;
    }

    public abstract ItemStack getItemStack();

    public abstract boolean compareItemStack(ItemStack other, boolean exactMatch);
}
