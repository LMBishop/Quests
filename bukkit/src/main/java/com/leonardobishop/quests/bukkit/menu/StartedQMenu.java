package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
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

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final HashMap<Integer, String> slotsToQuestIds = new HashMap<>();
    private final int pageSize = 45;
    private final QPlayer owner;

    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;

    public StartedQMenu(BukkitQuestsPlugin plugin, QPlayer owner) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.owner = owner;
    }

    public void populate(List<QuestSortWrapper> quests) {
        Collections.sort(quests);
        int slot = 0;
        for (QuestSortWrapper quest : quests) {
            if (owner.hasStartedQuest(quest.getQuest())) {
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
        String title = Chat.color(config.getString("options.guinames.quests-started-menu"));

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        ItemStack none = config.getItem("gui.no-started-quests");

        Inventory inventory = Bukkit.createInventory(null, 54, title);

        int invSlot = 0;
        if (!slotsToQuestIds.isEmpty()) {
            for (int pointer = pageMin; pointer < pageMax; pointer++) {
                if (slotsToQuestIds.containsKey(pointer)) {
                    Quest quest = plugin.getQuestManager().getQuestById(slotsToQuestIds.get(pointer));
                    QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);

                    inventory.setItem(invSlot, MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(),
                            plugin.getQItemStackRegistry().getQuestItemStack(quest).toItemStack(quest, owner, questProgress)));
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
        pageIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-desc"), pageplaceholders);
        pageIs.setAmount(Math.min(page, 64));
        pagePrevIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-prev"), pageplaceholders);
        pageNextIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-next"), pageplaceholders);

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
        } else if (config.getBoolean("options.trim-gui-size") && page == 1) {
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
    public boolean handleClick(InventoryClickEvent event, MenuController controller) {
        if (pagePrevLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage - 1);
            return true;
        } else if (pageNextLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage + 1);
            return true;
        } else if (event.getSlot() < pageSize && slotsToQuestIds.containsKey(event.getSlot() + ((currentPage) - 1) * pageSize)) {

            // repeat from above
            String questid = slotsToQuestIds.get(event.getSlot() + (((currentPage) - 1) * pageSize));
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (event.getClick() == ClickType.MIDDLE && config.getBoolean("options.allow-quest-track")) {
                MenuUtils.handleMiddleClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                return true;
            } else if (event.getClick() == ClickType.RIGHT) {
                MenuUtils.handleRightClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                return true;
            }
        }
        return false;
    }
}
