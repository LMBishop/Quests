package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmCancelMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final QPlayer owner;
    private final Quest quest;
    private final QMenu returnMenu;

    public ConfirmCancelMenuElement(BukkitQuestsPlugin plugin, QPlayer owner, Quest quest, QMenu returnMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.quest = quest;
        this.returnMenu = returnMenu;
    }

    @Override
    public ItemStack asItemStack() {
        return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), ((BukkitQuestsConfig) plugin.getQuestsConfig()).getItem("gui.quest-cancel-yes"));
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            if (owner.cancelQuest(quest)) {
                if (returnMenu != null) {
                    plugin.getMenuController().openMenu(owner.getPlayerUUID(), returnMenu);
                } else {
                    return ClickResult.CLOSE_MENU;
                }
            }
        }
        return ClickResult.DO_NOTHING;
    }
}
