package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.menu.QMenu;
import com.leonardobishop.quests.menu.QMenuCancel;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Options;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MenuController implements Listener {

    private static final HashMap<UUID, QMenu> tracker = new HashMap<>();
    private static final ArrayList<UUID> buffer = new ArrayList<>();

    private final Quests plugin;

    public MenuController(Quests plugin) {
        this.plugin = plugin;
    }

    /**
     * Add a player to the tracker so the event can watch them in the menu.
     *
     * @param uuid  UUID of player to track
     * @param qMenu The menu they have open
     */
    public static void track(UUID uuid, QMenu qMenu) {
        tracker.put(uuid, qMenu);
    }

    public HashMap<UUID, QMenu> getTracker() {
        return tracker;
    }

    public ArrayList<UUID> getBuffer() {
        return buffer;
    }

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
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

    // utility
    public void middleClickQuest(QMenu menu, Quest quest, Player player) {
        if (menu.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
            String tracked = menu.getOwner().getQuestProgressFile().getPlayerPreferences().getTrackedQuestId();

            if (quest.getId().equals(tracked)) {
                menu.getOwner().getQuestProgressFile().trackQuest(null);
            } else {
                menu.getOwner().getQuestProgressFile().trackQuest(quest);
            }
            player.closeInventory();
        }
    }

    // utility
    public void rightClickQuest(QMenu menu, Quest quest, Player player) {
        if (menu.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
            if (Options.QUEST_AUTOSTART.getBooleanValue()) return;
            QMenuCancel qMenuCancel = new QMenuCancel(menu.getOwner(), menu, quest);
            buffer.add(player.getUniqueId());
            player.openInventory(qMenuCancel.toInventory(1));
            tracker.put(player.getUniqueId(), qMenuCancel);
        }
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        // the buffer prevents players being lost from the tracker when changing menus, add to the buffer before
        // an anticipated menu change
        if (buffer.contains(event.getPlayer().getUniqueId())) {
            buffer.remove(event.getPlayer().getUniqueId());
        } else tracker.remove(event.getPlayer().getUniqueId());
    }
}
