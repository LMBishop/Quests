package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.PaginatedQMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PageDescMenuElement extends MenuElement {

    private final BukkitQuestsConfig config;
    private final PaginatedQMenu menu;

    public PageDescMenuElement(BukkitQuestsConfig config, PaginatedQMenu menu) {
        this.config = config;
        this.menu = menu;
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack is = config.getItem("gui.page-desc");
        is.setAmount(menu.getCurrentPage());

        return MenuUtils.applyPlaceholders(null, null,
                is,
                MenuUtils.fillPagePlaceholders(menu.getCurrentPage()));
    }

    @Override
    public ClickResult handleClick(ClickType clickType) {
        return ClickResult.DO_NOTHING;
    }
    
    public int getSlot() {
    	return config.getInt("gui.page-desc.slot", 49);
    }
    
    @Override
    public boolean isEnabled() {
    	return config.getBoolean("gui.page-desc.enabled", true);
    }
}
