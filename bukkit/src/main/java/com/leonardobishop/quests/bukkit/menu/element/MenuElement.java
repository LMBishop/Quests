package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.menu.ClickResult;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class MenuElement {

    public abstract ItemStack asItemStack();

    /**
     * Handle a click.
     *
     * @param clickType the type of click
     * @return whether the calling menu should be refreshed
     */
    public abstract ClickResult handleClick(ClickType clickType);

    public boolean isEnabled() {
        return true;
    }
}
