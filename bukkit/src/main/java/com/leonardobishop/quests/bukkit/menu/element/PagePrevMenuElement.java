package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.PaginatedQMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PagePrevMenuElement extends MenuElement {

    private final BukkitQuestsConfig config;
    private final PaginatedQMenu menu;
    public PagePrevMenuElement(BukkitQuestsConfig config, PaginatedQMenu menu) {
        this.config = config;
        this.menu = menu;
    }

    @Override
    public ItemStack asItemStack() {
        // hide if on first page
        if (menu.getCurrentPage() == menu.getMinPage()) {
            return new ItemStack(Material.AIR);
        }

        return MenuUtils.applyPlaceholders(null, null,
            config.getItem("gui.page-prev"),
            MenuUtils.fillPagePlaceholders(menu.getCurrentPage()));
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (menu.getCurrentPage() == menu.getMinPage()) {
            return ClickResult.DO_NOTHING;
        }

        menu.setCurrentPage(menu.getCurrentPage() - 1);
        return ClickResult.REFRESH_PANE;
    }

    public int getSlot() {
        return config.getInt("gui.page-prev.slot", 48);
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("gui.page-prev.enabled", true);
    }
}
