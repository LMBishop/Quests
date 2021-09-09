package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CustomMenuElement extends MenuElement{

    private final ItemStack itemStack;

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack) {
        this.itemStack = MenuUtils.applyPlaceholders(plugin, owner, itemStack);
    }

    @Override
    public ItemStack asItemStack() {
        return itemStack;
    }
}
