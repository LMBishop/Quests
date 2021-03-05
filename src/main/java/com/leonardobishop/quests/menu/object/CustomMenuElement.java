package com.leonardobishop.quests.menu.object;

import org.bukkit.inventory.ItemStack;

public class CustomMenuElement extends MenuElement{

    private ItemStack itemStack;

    public CustomMenuElement(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack asItemStack() {
        return itemStack;
    }
}
