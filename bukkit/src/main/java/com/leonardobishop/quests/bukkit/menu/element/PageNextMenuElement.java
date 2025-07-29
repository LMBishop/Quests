package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.PaginatedQMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PageNextMenuElement extends MenuElement {

    private final BukkitQuestsConfig config;
    private final PaginatedQMenu menu;
    public PageNextMenuElement(BukkitQuestsConfig config, PaginatedQMenu menu) {
        this.config = config;
        this.menu = menu;
    }

    @Override
    public ItemStack asItemStack() {
        // hide if on last page
        if (menu.getCurrentPage() == menu.getMaxPage()) {
            return new ItemStack(Material.AIR);
        }

        return MenuUtils.applyPlaceholders(null, null,
            config.getItem("gui.page-next"),
            MenuUtils.fillPagePlaceholders(menu.getCurrentPage()));
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (menu.getCurrentPage() == menu.getMaxPage()) {
            return ClickResult.DO_NOTHING;
        }

        menu.setCurrentPage(menu.getCurrentPage() + 1);
        return ClickResult.REFRESH_PANE;
    }

    public int getSlot() {
        return config.getInt("gui.page-next.slot", 50);
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("gui.page-next.enabled", true);
    }
}
