package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.menu.QMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.UUID;

public class MenuController implements Listener {

    private final HashMap<UUID, QMenu> tracker = new HashMap<>();
    private final Quests plugin;

    public MenuController(Quests plugin) {
        this.plugin = plugin;
    }

    public void openMenu(HumanEntity player, QMenu qMenu, int page) {
        player.openInventory(qMenu.toInventory(page));
        tracker.put(player.getUniqueId(), qMenu);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        tracker.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        // check if the player has a quest menu open
        if (tracker.containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null)
                return; //The player clicked outside the inventory
            if (event.getClickedInventory().getType() == InventoryType.PLAYER)
                return; //The clicked inventory is a player inventory type

            QMenu qMenu = tracker.get(event.getWhoClicked().getUniqueId());
            qMenu.handleClick(event, this);
        }
    }

}
