package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.menu.object.CustomMenuElement;
import com.leonardobishop.quests.menu.object.MenuElement;
import com.leonardobishop.quests.menu.object.QuestMenuElement;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Items;
import com.leonardobishop.quests.util.Options;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Menu for a specific category.
 */
public class QMenuQuest implements QMenu {

    private final Quests plugin;
    private final HashMap<Integer, MenuElement> menuElements = new HashMap<>();
    private final QMenuCategory superMenu;
    private final String categoryName;
    private final int pageSize = 45;
    private final QPlayer owner;

    private int maxElement = 0;
    private int backButtonLocation = -1;
    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;
    private boolean backButtonEnabled = true;

    public QMenuQuest(Quests plugin, QPlayer owner, String categoryName, QMenuCategory superMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.categoryName = categoryName;
        this.superMenu = superMenu;
    }

    public void populate(List<Quest> quests) {
        String path;
        if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
            path = "custom-elements.c:" + categoryName;
        } else {
            path = "custom-elements.quests";
        }
        if (plugin.getConfig().isConfigurationSection(path)) {
            for (String s : plugin.getConfig().getConfigurationSection(path).getKeys(false)) {
                if (!NumberUtils.isNumber(s)) continue;
                int slot = Integer.parseInt(s);
                int repeat = plugin.getConfig().getInt(path + "." + s + ".repeat");
                ItemStack is = plugin.getItemStack(path + "." + s + ".display", plugin.getConfig());

                for (int i = 0; i <= repeat; i++) {
                    menuElements.put(slot + i, new CustomMenuElement(is));
                }
            }
        }

        Collections.sort(quests);
        int slot = 0;
        for (Quest quest : quests) {
            while (menuElements.containsKey(slot)) slot++;
            if (Options.GUI_HIDE_LOCKED.getBooleanValue()) {
                QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
                long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
                if (!owner.getQuestProgressFile().hasMetRequirements(quest) || (!quest.isRepeatable() && questProgress.isCompletedBefore()) || cooldown > 0) {
                    continue;
                }
            }
            if (Options.GUI_HIDE_QUESTS_NOPERMISSION.getBooleanValue() && quest.isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getUuid()).hasPermission("quests.quest." + quest.getId())) {
                    continue;
                }
            }
            menuElements.put(slot, new QuestMenuElement(plugin, owner, quest.getId()));
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

    public String getCategoryName() {
        return categoryName;
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
        String title = Options.color(Options.GUITITLE_QUESTS.getStringValue());

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        ItemStack back = Items.BACK_BUTTON.getItem();

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
        pageIs = replaceItemStack(Items.PAGE_DESCRIPTION.getItem(), pageplaceholders);
        pageIs.setAmount(Math.min(page, 64));
        pagePrevIs = replaceItemStack(Items.PAGE_PREV.getItem(), pageplaceholders);
        pageNextIs = replaceItemStack(Items.PAGE_NEXT.getItem(), pageplaceholders);

        if (Options.CATEGORIES_ENABLED.getBooleanValue() && backButtonEnabled) {
            inventory.setItem(45, back);
            backButtonLocation = 45;
        }
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
            } else if (Options.CATEGORIES_ENABLED.getBooleanValue() && backButtonEnabled) {
                inventorySize += 9;
            }

            Inventory trimmedInventory = Bukkit.createInventory(null, inventorySize, title);

            for (int slot = 0; slot < trimmedInventory.getSize(); slot++) {
                if (slot >= (trimmedInventory.getSize() - 9) && backButtonEnabled){
                    if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
                        trimmedInventory.setItem(slot, back);
                        backButtonLocation = slot;
                    }
                    break;
                }
                trimmedInventory.setItem(slot, inventory.getItem(slot));
            }
            return trimmedInventory;
        }

        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event, MenuController controller) {
        //TODO make everything a menu element cuz this is jank
        if (pagePrevLocation == event.getSlot()) {
            controller.getBuffer().add(event.getWhoClicked().getUniqueId());
            event.getWhoClicked().openInventory(this.toInventory(currentPage - 1));

        } else if (pageNextLocation == event.getSlot()) {
            controller.getBuffer().add(event.getWhoClicked().getUniqueId());
            event.getWhoClicked().openInventory(this.toInventory(currentPage + 1));

        } else if (Options.CATEGORIES_ENABLED.getBooleanValue() && backButtonLocation == event.getSlot()) {
            QMenuCategory qMenuCategory = this.getSuperMenu();
            controller.getBuffer().add(event.getWhoClicked().getUniqueId());
            event.getWhoClicked().openInventory(qMenuCategory.toInventory(1));
            controller.getTracker().put(event.getWhoClicked().getUniqueId(), qMenuCategory);

        } else if (event.getSlot() < pageSize && menuElements.containsKey(event.getSlot() + (((currentPage) - 1) * pageSize))) {
            MenuElement menuElement = menuElements.get(event.getSlot() + ((currentPage - 1) * pageSize));
            if (menuElement instanceof QuestMenuElement) {
                QuestMenuElement questMenuElement = (QuestMenuElement) menuElement;
                Quest quest = plugin.getQuestManager().getQuestById(questMenuElement.getQuestId());
                if (event.getClick() == ClickType.LEFT) {
                    if (Options.QUEST_AUTOSTART.getBooleanValue()) return;
                    if (this.getOwner().getQuestProgressFile().startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                        event.getWhoClicked().closeInventory(); //TODO Option to keep the menu open
                    }
                } else if (event.getClick() == ClickType.MIDDLE && Options.ALLOW_QUEST_TRACK.getBooleanValue()) {
                    controller.middleClickQuest(this, quest, Bukkit.getPlayer(this.getOwner().getUuid()));
                } else if (event.getClick() == ClickType.RIGHT && Options.ALLOW_QUEST_CANCEL.getBooleanValue()
                        && this.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
                    controller.rightClickQuest(this, quest, Bukkit.getPlayer(this.getOwner().getUuid()));
                }
            }
        }
    }

    public boolean isBackButtonEnabled() {
        return backButtonEnabled;
    }

    public void setBackButtonEnabled(boolean backButtonEnabled) {
        this.backButtonEnabled = backButtonEnabled;
    }

    public int getBackButtonLocation() {
        return backButtonLocation;
    }

    public QMenuCategory getSuperMenu() {
        return superMenu;
    }

    public ItemStack replaceItemStack(ItemStack is) {
        return replaceItemStack(is, Collections.emptyMap());
    }

    public ItemStack replaceItemStack(ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = Bukkit.getPlayer(owner.getUuid());
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                    if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                        s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    }
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
            if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            }
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }
}
