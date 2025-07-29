package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.menu.ClickResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * literally has the sole purpose of returning Material.AIR
 */
public class SpacerMenuElement extends MenuElement {

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        return ClickResult.DO_NOTHING;
    }
}
