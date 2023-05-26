package com.leonardobishop.quests.bukkit.item;

import org.bukkit.inventory.ItemStack;

public class ParsedQuestItem extends QuestItem {

    private final ItemStack itemStack;

    public ParsedQuestItem(String type, String id, ItemStack itemStack) {
        super(type, id);
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack()  {
        return itemStack;
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        return exactMatch ? other.isSimilar(itemStack) : other.getType() == itemStack.getType();
    }
}
