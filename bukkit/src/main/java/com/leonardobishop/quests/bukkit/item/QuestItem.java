package com.leonardobishop.quests.bukkit.item;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a single quest item.
 */
public class QuestItem {

    private final String id;
    private final ItemStack itemStack;

    public QuestItem(String id, ItemStack itemStack) {
        this.id = id;
        this.itemStack = itemStack;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
