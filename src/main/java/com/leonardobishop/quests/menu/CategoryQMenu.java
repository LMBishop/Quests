package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.menu.element.CategoryMenuElement;
import com.leonardobishop.quests.menu.element.CustomMenuElement;
import com.leonardobishop.quests.menu.element.MenuElement;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.util.Items;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a menu which contains a listing of different categories.
 */
public class CategoryQMenu implements QMenu {

    private final Quests plugin;
    private final int pageSize = 45;
    private final HashMap<Integer, MenuElement> menuElements = new HashMap<>();
    private final QPlayer owner;

    private int maxElement = 0;
    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;

    public CategoryQMenu(Quests plugin, QPlayer owner) {
        this.plugin = plugin;
        this.owner = owner;
    }

    public void populate(List<QuestQMenu> menuQuests) {
        if (plugin.getConfig().isConfigurationSection("custom-elements.categories")) {
            for (String s : plugin.getConfig().getConfigurationSection("custom-elements.categories").getKeys(false)) {
                if (!NumberUtils.isNumber(s)) continue;
                int slot = Integer.parseInt(s);
                int repeat = plugin.getConfig().getInt("custom-elements.categories." + s + ".repeat");
                ItemStack is = plugin.getItemStack("custom-elements.categories." + s + ".display", plugin.getConfig());

                for (int i = 0; i <= repeat; i++) {
                    menuElements.put(slot + i, new CustomMenuElement(is));
                }
            }
        }
        int slot = 0;
        for (QuestQMenu questQMenu : menuQuests) {
            while (menuElements.containsKey(slot)) slot++;
            if (Options.GUI_HIDE_CATEGORIES_NOPERMISSION.getBooleanValue() && plugin.getQuestManager().getCategoryById(questQMenu.getCategoryName()).isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getUuid()).hasPermission("quests.category." + questQMenu.getCategoryName())) {
                    continue;
                }
            }
            menuElements.put(slot, new CategoryMenuElement(plugin, owner.getUuid(), questQMenu));
            slot++;
        }

        for (Integer integer : menuElements.keySet()) {
            if (integer + 1 > maxElement) maxElement = integer + 1;
        }
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public Inventory toInventory(int page) {
        currentPage = page;
        int pageMin = pageSize * (page - 1);
        int pageMax = pageSize * page;
        String title = Options.color(Options.GUITITLE_QUESTS_CATEGORY.getStringValue());

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        int highestOnPage = 0;
        for (int pointer = pageMin; pointer < pageMax; pointer++) {
            if (menuElements.containsKey(pointer)) {
                inventory.setItem(pointer - ((page - 1) * pageSize), menuElements.get(pointer).asItemStack());
                if (pointer + 1 > highestOnPage) highestOnPage = pointer + 1;
            }
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

        if (maxElement > pageSize) {
            inventory.setItem(49, pageIs);
            if (page != 1) {
                inventory.setItem(48, pagePrevIs);
                pagePrevLocation = 48;
            }
            if (Math.ceil((double) maxElement / ((double) pageSize)) != page) {
                inventory.setItem(50, pageNextIs);
                pageNextLocation = 50;
            }
        } else if (Options.TRIM_GUI_SIZE.getBooleanValue() && page == 1) {
            int inventorySize = highestOnPage + (9 - highestOnPage % 9) * Math.min(1, highestOnPage % 9);
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

        } else if (event.getSlot() < pageSize && menuElements.containsKey(event.getSlot() + (((currentPage) - 1) * pageSize))) {
            MenuElement element = menuElements.get(event.getSlot() + ((currentPage - 1) * pageSize));
            if (element instanceof CategoryMenuElement) {
                CategoryMenuElement categoryMenuElement = (CategoryMenuElement) element;
                QuestQMenu questQMenu = categoryMenuElement.getQuestMenu();
                if (owner.openCategory(plugin.getQuestManager().getCategoryById(questQMenu.getCategoryName()), questQMenu) != 0) {
                    event.getWhoClicked().sendMessage(Messages.QUEST_CATEGORY_PERMISSION.getMessage());
                }
            }
        }
    }
}
