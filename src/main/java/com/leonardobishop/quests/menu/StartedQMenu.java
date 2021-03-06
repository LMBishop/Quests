package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Items;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a menu listing quests the player has started.
 */
public class StartedQMenu implements QMenu {

    private final Quests plugin;
    private final HashMap<Integer, String> slotsToQuestIds = new HashMap<>();
    private final int pageSize = 45;
    private final QPlayer owner;

    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;

    public StartedQMenu(Quests plugin, QPlayer owner) {
        this.plugin = plugin;
        this.owner = owner;
    }

    public void populate(List<QuestSortWrapper> quests) {
        Collections.sort(quests);
        int slot = 0;
        for (QuestSortWrapper quest : quests) {
            if (owner.getQuestProgressFile().hasStartedQuest(quest.getQuest())) {
                slotsToQuestIds.put(slot, quest.getQuest().getId());
                slot++;
            }
        }
    }

    public HashMap<Integer, String> getSlotsToMenu() {
        return slotsToQuestIds;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public int getPagePrevLocation() {
        return pagePrevLocation;
    }

    public int getPageNextLocation() {
        return pageNextLocation;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Inventory toInventory(int page) {
        currentPage = page;
        int pageMin = pageSize * (page - 1);
        int pageMax = pageSize * page;
        String title = Options.color(Options.GUITITLE_QUESTS_STARTED.getStringValue());

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        ItemStack none = Items.NO_STARTED_QUESTS.getItem();

        Inventory inventory = Bukkit.createInventory(null, 54, title);

        int invSlot = 0;
        if (!slotsToQuestIds.isEmpty()) {
            for (int pointer = pageMin; pointer < pageMax; pointer++) {
                if (slotsToQuestIds.containsKey(pointer)) {
                    Quest quest = plugin.getQuestManager().getQuestById(slotsToQuestIds.get(pointer));
                    QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);

                    inventory.setItem(invSlot, MenuUtil.applyPlaceholders(plugin, owner.getUuid(), plugin.getQuestManager().getQuestById(
                            quest.getId()).getDisplayItem().toItemStack(quest, owner.getQuestProgressFile(), questProgress)));
                }
                invSlot++;
            }
        } else {
            inventory.setItem(4, none);
        }

        pageNextLocation = -1;
        pagePrevLocation = -1;

        Map<String, String> pageplaceholders = new HashMap<>();
        pageplaceholders.put("{prevpage}", String.valueOf(page - 1));
        pageplaceholders.put("{nextpage}", String.valueOf(page + 1));
        pageplaceholders.put("{page}", String.valueOf(page));
        pageIs = MenuUtil.applyPlaceholders(plugin, owner.getUuid(), Items.PAGE_DESCRIPTION.getItem(), pageplaceholders);
        pageIs.setAmount(Math.min(page, 64));
        pagePrevIs = MenuUtil.applyPlaceholders(plugin, owner.getUuid(), Items.PAGE_PREV.getItem(), pageplaceholders);
        pageNextIs = MenuUtil.applyPlaceholders(plugin, owner.getUuid(), Items.PAGE_NEXT.getItem(), pageplaceholders);

        if (slotsToQuestIds.size() > pageSize) {
            inventory.setItem(49, pageIs);
            if (page != 1) {
                inventory.setItem(48, pagePrevIs);
                pagePrevLocation = 48;
            }
            if (Math.ceil((double) slotsToQuestIds.size() / ((double) 45)) != page) {
                inventory.setItem(50, pageNextIs);
                pageNextLocation = 50;
            }
        } else if (Options.TRIM_GUI_SIZE.getBooleanValue() && page == 1) {
            int slotsUsed = 0;
            for (int pointer = 0; pointer < pageMax; pointer++) {
                if (inventory.getItem(pointer) != null) {
                    slotsUsed++;
                }
            }

            int inventorySize = (slotsUsed >= 54) ? 54 : slotsUsed + (9 - slotsUsed % 9) * Math.min(1, slotsUsed % 9);
            inventorySize = inventorySize <= 0 ? 9 : inventorySize;
            if (inventorySize == 54) {
                return inventory;
            }

            Inventory trimmedInventory = Bukkit.createInventory(null, inventorySize, title);

            for (int slot = 0; slot < trimmedInventory.getSize(); slot++) {
                trimmedInventory.setItem(slot, inventory.getItem(slot));
            }
            return trimmedInventory;
        }

        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event, MenuController controller) {
        if (pagePrevLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage - 1);

        } else if (pageNextLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage + 1);

        } else if (event.getSlot() < pageSize && slotsToQuestIds.containsKey(event.getSlot() + ((currentPage) - 1) * pageSize)) {

            // repeat from above
            String questid = slotsToQuestIds.get(event.getSlot() + (((currentPage) - 1) * pageSize));
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (event.getClick() == ClickType.MIDDLE && Options.ALLOW_QUEST_TRACK.getBooleanValue()) {
                MenuUtil.handleMiddleClick(this, quest, Bukkit.getPlayer(owner.getUuid()), controller);
            } else if (event.getClick() == ClickType.RIGHT && Options.ALLOW_QUEST_CANCEL.getBooleanValue()
                    && owner.getQuestProgressFile().hasStartedQuest(quest)) {
                MenuUtil.handleRightClick(this, quest, Bukkit.getPlayer(owner.getUuid()), controller);
            }
        }
    }
}
