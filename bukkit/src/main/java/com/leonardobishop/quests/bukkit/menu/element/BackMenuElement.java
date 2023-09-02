package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.MenuController;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * literally has the sole purpose of returning Material.AIR
 */
public class BackMenuElement extends MenuElement {

    private final BukkitQuestsConfig config;
    private final UUID player;
    private final MenuController controller;
    private final QMenu previousMenu;

    public BackMenuElement(BukkitQuestsConfig config, UUID player, MenuController controller, QMenu previousMenu) {
        this.config = config;
        this.player = player;
        this.controller = controller;
        this.previousMenu = previousMenu;
    }

    @Override
    public ItemStack asItemStack() {
        return config.getItem("gui.back-button");
    }

    @Override
    public ClickResult handleClick(ClickType clickType) {
        controller.openMenu(player, previousMenu);
        return ClickResult.DO_NOTHING;
    }
    
    public int getSlot() {
        return config.getInt("gui.back-button.slot", 45);
    }
    
    @Override
    public boolean isEnabled() {
        return config.getBoolean("gui.back-button.enabled", true);
    }
}
