package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.MenuController;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * literally has the sole purpose of returning Material.AIR
 */
public class BackMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final UUID player;
    private final MenuController controller;
    private final QMenu previousMenu;

    public BackMenuElement(BukkitQuestsPlugin plugin, UUID player, MenuController controller, QMenu previousMenu) {
        this.plugin = plugin;
        this.player = player;
        this.controller = controller;
        this.previousMenu = previousMenu;
    }

    @Override
    public ItemStack asItemStack() {
        return MenuUtils.applyPlaceholders(plugin, player, ((BukkitQuestsConfig) plugin.getQuestsConfig()).getItem("gui.back-button"));
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        controller.openMenu(player, previousMenu);
        return ClickResult.DO_NOTHING;
    }
    
    public int getSlot() {
        return plugin.getQuestsConfig().getInt("gui.back-button.slot", 45);
    }
    
    @Override
    public boolean isEnabled() {
        return plugin.getQuestsConfig().getBoolean("gui.back-button.enabled", true);
    }
}
