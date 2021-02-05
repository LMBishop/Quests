package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.obj.misc.QMenu;
import com.leonardobishop.quests.obj.misc.QMenuCancel;
import com.leonardobishop.quests.obj.misc.QMenuCategory;
import com.leonardobishop.quests.obj.misc.QMenuQuest;
import com.leonardobishop.quests.quests.Quest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EventInventory implements Listener {

    private static final HashMap<UUID, QMenu> tracker = new HashMap<>();
    private final Quests plugin;

    // ADD PLAYERS TO THE BUFFER BEFORE AN ANTICIPATED MENU CHANGE SO THAT
    // THEY ARE NOT LOST FROM THE TRACKER WHEN CHANGING MENUS
    private final ArrayList<UUID> buffer = new ArrayList<>();

    public EventInventory(Quests plugin) {
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

            // **** MENU TYPE: QUESTS IN CATEGORY/ALL QUESTS ****
            if (qMenu instanceof QMenuQuest) {
                QMenuQuest qMenuQuest = (QMenuQuest) qMenu;

                if (qMenuQuest.getPagePrevLocation() == event.getSlot()) {
                    // (see line 26)
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(qMenuQuest.getCurrentPage() - 1));

                } else if (qMenuQuest.getPageNextLocation() == event.getSlot()) {
                    //This shouldn't be here lol, forgot about it
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(qMenuQuest.getCurrentPage() + 1));

                    // return to QMenuCategory (category listing)
                } else if (Options.CATEGORIES_ENABLED.getBooleanValue() && qMenuQuest.getBackButtonLocation() == event.getSlot()) {
                    QMenuCategory qMenuCategory = qMenuQuest.getSuperMenu();
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuCategory.toInventory(1));
                    tracker.put(event.getWhoClicked().getUniqueId(), qMenuCategory);

                    // handle when player wishes to start a quest by matching the slot they clicked to a predetermined
                    // map which maps quests to slots so you do not have to compare the item stack
                } else if (event.getSlot() < qMenuQuest.getPageSize() && qMenuQuest.getSlotsToMenu().containsKey(event.getSlot() + (((qMenuQuest
                        .getCurrentPage()) - 1) * qMenuQuest.getPageSize()))) {
                    if (Options.QUEST_AUTOSTART.getBooleanValue()) return;

                    String questid = qMenuQuest.getSlotsToMenu().get(event.getSlot() + (((qMenuQuest.getCurrentPage()) - 1) * qMenuQuest.getPageSize()));
                    Quest quest = plugin.getQuestManager().getQuestById(questid);
                    if (event.getClick() == ClickType.LEFT) {
                        if (qMenuQuest.getOwner().getQuestProgressFile().startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                            event.getWhoClicked().closeInventory(); //TODO Option to keep the menu open
                        }
                    } else if (event.getClick() == ClickType.RIGHT && Options.ALLOW_QUEST_CANCEL.getBooleanValue()
                            && qMenuQuest.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
                        QMenuCancel qMenuCancel = new QMenuCancel(qMenuQuest.getOwner(), qMenuQuest, quest);
                        buffer.add(event.getWhoClicked().getUniqueId());
                        event.getWhoClicked().openInventory(qMenuCancel.toInventory());
                        tracker.put(event.getWhoClicked().getUniqueId(), qMenuCancel);
                    }
                }

                // **** MENU TYPE: CATEGORY LISTING ****
            } else if (qMenu instanceof QMenuCategory) {
                QMenuCategory qMenuCategory = (QMenuCategory) qMenu;

                if (qMenuCategory.getSlotsToMenu().containsKey(event.getSlot())) {
                    QMenuQuest qMenuQuest = qMenuCategory.getSlotsToMenu().get(event.getSlot());
                    buffer.add(event.getWhoClicked().getUniqueId());
                    if (qMenuCategory.getOwner().openCategory(plugin.getQuestManager().getCategoryById(qMenuQuest.getCategoryName()), qMenuQuest) != 0) {
                        buffer.remove(event.getWhoClicked().getUniqueId());
                        event.getWhoClicked().sendMessage(Messages.QUEST_CATEGORY_PERMISSION.getMessage());
                    }
                }

                // **** MENU TYPE: CANCELLING QUEST MENU ****
            } else if (qMenu instanceof QMenuCancel) {
                QMenuCancel qMenuCancel = (QMenuCancel) qMenu;

                event.setCancelled(true);
                if (event.getSlot() == 10 || event.getSlot() == 11 || event.getSlot() == 12) {
                    QMenuQuest qMenuQuest = qMenuCancel.getSuperMenu();
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(1));
                    tracker.put(event.getWhoClicked().getUniqueId(), qMenuQuest);
                } else if (event.getSlot() == 14 || event.getSlot() == 15 || event.getSlot() == 16) {
                    if (qMenuCancel.getOwner().getQuestProgressFile().cancelQuest(qMenuCancel.getQuest())) {
                        event.getWhoClicked().closeInventory();
                    }
                }
            }
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
