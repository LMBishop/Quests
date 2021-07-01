package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.CategoryMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.CustomMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.SpacerMenuElement;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
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

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final HashMap<Integer, MenuElement> menuElements = new HashMap<>();
    private final QPlayer owner;

    private int pageSize = 45;
    private int maxElement = 0;
    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;

    public CategoryQMenu(BukkitQuestsPlugin plugin, QPlayer owner) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.owner = owner;
    }

    public void populate(List<QuestQMenu> menuQuests) {
        if (config.getConfig().isConfigurationSection("custom-elements.categories")) {
            for (String s : config.getConfig().getConfigurationSection("custom-elements.categories").getKeys(false)) {
                if (!NumberUtils.isNumber(s)) continue;
                int slot = Integer.parseInt(s);
                int repeat = config.getInt("custom-elements.categories." + s + ".repeat");
                MenuElement menuElement;
                if (config.getConfig().contains("custom-elements.categories." + s + ".display")) {
                    ItemStack is = config.getItem("custom-elements.categories." + s + ".display");
                    menuElement = new CustomMenuElement(is);
                } else if (config.getBoolean("custom-elements.categories." + s + ".spacer", false)) {
                    menuElement = new SpacerMenuElement();
                } else continue; // user = idiot

                for (int i = 0; i <= repeat; i++) {
                    menuElements.put(slot + i, menuElement);
                }
            }
        }
        int slot = 0;
        for (QuestQMenu questQMenu : menuQuests) {
            while (menuElements.containsKey(slot)) slot++;
            if (config.getBoolean("options.gui-hide-categories-nopermission") && plugin.getQuestManager().getCategoryById(questQMenu.getCategoryName()).isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getPlayerUUID()).hasPermission("quests.category." + questQMenu.getCategoryName())) {
                    continue;
                }
            }
            menuElements.put(slot, new CategoryMenuElement(plugin, owner.getPlayerUUID(), questQMenu));
            slot++;
        }

        for (Integer integer : menuElements.keySet()) {
            if (integer + 1 > maxElement) maxElement = integer + 1;
        }

        // stop bottom row of pg1 going to pg2 if entire inv contents would fit on pg1 perfectly
        if (maxElement > 45 && maxElement <= 54) {
            pageSize = 54;
        } else {
            pageSize = 45;
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
        String title = Chat.color(config.getString("options.guinames.quests-category"));

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
        pageIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-desc"), pageplaceholders);
        pageIs.setAmount(Math.min(page, 64));
        pagePrevIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-prev"), pageplaceholders);
        pageNextIs = MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), config.getItem("gui.page-next"), pageplaceholders);

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
        } else if (config.getBoolean("options.trim-gui-size") && page == 1) {
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
    public boolean handleClick(InventoryClickEvent event, MenuController controller) {
        if (pagePrevLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage - 1);
            return true;

        } else if (pageNextLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage + 1);
            return true;

        } else if (event.getSlot() < pageSize && menuElements.containsKey(event.getSlot() + (((currentPage) - 1) * pageSize))) {
            MenuElement element = menuElements.get(event.getSlot() + ((currentPage - 1) * pageSize));
            if (element instanceof CategoryMenuElement) {
                CategoryMenuElement categoryMenuElement = (CategoryMenuElement) element;
                QuestQMenu questQMenu = categoryMenuElement.getQuestMenu();
                if (plugin.getMenuController().openQuestCategory(owner,
                        plugin.getQuestManager().getCategoryById(questQMenu.getCategoryName()), questQMenu) != 0) {
                    event.getWhoClicked().sendMessage(Messages.QUEST_CATEGORY_PERMISSION.getMessage());
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
