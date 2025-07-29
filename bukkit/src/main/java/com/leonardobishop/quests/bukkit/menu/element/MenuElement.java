package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.menu.ClickResult;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class MenuElement {

    public abstract ItemStack asItemStack();

    /**
     * Handle a click.
     *
     * @param whoClicked who clicked
     * @param clickType the type of click
     * @return whether the calling menu should be refreshed
     */
    public abstract ClickResult handleClick(Player whoClicked, ClickType clickType);

    public boolean isEnabled() {
        return true;
    }
}
