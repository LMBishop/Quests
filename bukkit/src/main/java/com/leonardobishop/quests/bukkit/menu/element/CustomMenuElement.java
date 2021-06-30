package com.leonardobishop.quests.bukkit.menu.element;

import org.bukkit.inventory.ItemStack;

public class CustomMenuElement extends MenuElement{

    private final ItemStack itemStack;

    public CustomMenuElement(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack asItemStack() {
        return itemStack;
    }
}
