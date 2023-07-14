package com.leonardobishop.quests.bukkit.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderQuestItem extends QuestItem {

    private final String itemsAdderId;

    public ItemsAdderQuestItem(String id, String itemsAdderId) {
        super("itemsadder", id);
        this.itemsAdderId = itemsAdderId;
    }

    @Override
    public ItemStack getItemStack() {
        CustomStack stack = CustomStack.getInstance(itemsAdderId);
        if (stack != null) {
            return stack.getItemStack();
        }
        return null;
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        CustomStack stack = CustomStack.byItemStack(other);
        return stack != null && stack.getNamespacedID().equals(itemsAdderId);
    }
}
