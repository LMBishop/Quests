package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface QMenu {

    QPlayer getOwner();
    Inventory toInventory(int page);
    boolean handleClick(InventoryClickEvent event, MenuController controller);

}
