package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AbortCancelMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final QPlayer owner;
    private final QMenu returnMenu;

    public AbortCancelMenuElement(BukkitQuestsPlugin plugin, QPlayer owner, QMenu returnMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.returnMenu = returnMenu;
    }

    @Override
    public ItemStack asItemStack() {
        return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), ((BukkitQuestsConfig) plugin.getQuestsConfig()).getItem("gui.quest-cancel-no"));
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            if (returnMenu != null) {
                plugin.getMenuController().openMenu(owner.getPlayerUUID(), returnMenu);
            } else {
                return ClickResult.CLOSE_MENU;
            }
        }
        return ClickResult.DO_NOTHING;
    }
}
