package me.fatpigsarefat.quests.events;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.obj.Options;
import me.fatpigsarefat.quests.obj.misc.QMenu;
import me.fatpigsarefat.quests.obj.misc.QMenuCategory;
import me.fatpigsarefat.quests.obj.misc.QMenuQuest;
import me.fatpigsarefat.quests.quests.Quest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EventInventory implements Listener {

    private static HashMap<UUID, QMenu> tracker = new HashMap<>();
    private ArrayList<UUID> buffer = new ArrayList<>();

    public static void track(UUID uuid, QMenu qMenu) {
        tracker.put(uuid, qMenu);
    }

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        if (tracker.containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            QMenu qMenu = tracker.get(event.getWhoClicked().getUniqueId());

            if (qMenu instanceof QMenuQuest) {
                QMenuQuest qMenuQuest = (QMenuQuest) qMenu;

                if (qMenuQuest.getPagePrevLocation() == event.getSlot()) {
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(qMenuQuest.getCurrentPage() - 1));

                } else if (qMenuQuest.getPageNextLocation() == event.getSlot()) {
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(qMenuQuest.getCurrentPage() + 1));

                } else if (Options.CATEGORIES_ENABLED.getBooleanValue() && qMenuQuest.getBackButtonLocation() == event.getSlot()) {
                    QMenuCategory qMenuCategory = qMenuQuest.getSuperMenu();
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuCategory.toInventory(1));
                    tracker.put(event.getWhoClicked().getUniqueId(), qMenuCategory);

                } else if (event.getSlot() < qMenuQuest.getPageSize() && qMenuQuest.getSlotsToMenu().containsKey(event.getSlot() + (((qMenuQuest
                        .getCurrentPage()) - 1) * qMenuQuest.getPageSize()))) {
                    String questid = qMenuQuest.getSlotsToMenu().get(event.getSlot());
                    Quest quest = Quests.getQuestManager().getQuestById(questid);
                    if (event.getClick() == ClickType.LEFT) {
                        if (qMenuQuest.getOwner().getQuestProgressFile().startQuest(quest) == 0) {
                            event.getWhoClicked().closeInventory();
                        }
                    } else if (event.getClick() == ClickType.RIGHT) {
                        if (qMenuQuest.getOwner().getQuestProgressFile().cancelQuest(quest)) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                }
            } else if (qMenu instanceof QMenuCategory) {
                QMenuCategory qMenuCategory = (QMenuCategory) qMenu;

                if (qMenuCategory.getSlotsToMenu().containsKey(event.getSlot())) {
                    QMenuQuest qMenuQuest = qMenuCategory.getSlotsToMenu().get(event.getSlot());
                    buffer.add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().openInventory(qMenuQuest.toInventory(1));
                    tracker.put(event.getWhoClicked().getUniqueId(), qMenuQuest);
                }
            }
        }
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        if (buffer.contains(event.getPlayer().getUniqueId())) {
            buffer.remove(event.getPlayer().getUniqueId());
        } else if (tracker.containsKey(event.getPlayer().getUniqueId())) {
            tracker.remove(event.getPlayer().getUniqueId());
        }
    }

}
