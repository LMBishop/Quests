package com.leonardobishop.quests.menu.element;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * literally has the sole purpose of returning Material.AIR
 */
public class SpacerMenuElement extends MenuElement {

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(Material.AIR);
    }
}
