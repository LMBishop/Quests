package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.CustomMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.QuestMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.SpacerMenuElement;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a menu for a specified category (or all if they are disabled),
 * which contains a listing of different quests.
 */
public class QuestQMenu implements QMenu {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final HashMap<Integer, MenuElement> menuElements = new HashMap<>();
    private final CategoryQMenu superMenu;
    private final String categoryName;
    private final int pageSize = 45;
    private final QPlayer owner;

    private int maxElement = 0;
    private int backButtonLocation = -1;
    private int pagePrevLocation = -1;
    private int pageNextLocation = -1;
    private int currentPage = -1;
    private boolean backButtonEnabled = true;

    public QuestQMenu(BukkitQuestsPlugin plugin, QPlayer owner, String categoryName, CategoryQMenu superMenu) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.owner = owner;
        this.categoryName = categoryName;
        this.superMenu = superMenu;
    }

    public void populate(List<Quest> quests) {
        String path;
        if (config.getBoolean("options.categories-enabled")) {
            path = "custom-elements.c:" + categoryName;
        } else {
            path = "custom-elements.quests";
        }
        if (plugin.getConfig().isConfigurationSection(path)) {
            for (String s : plugin.getConfig().getConfigurationSection(path).getKeys(false)) {
                if (!NumberUtils.isNumber(s)) {
                    continue;
                }
                int slot = Integer.parseInt(s);
                int repeat = plugin.getConfig().getInt(path + "." + s + ".repeat");
                MenuElement menuElement;
                if (plugin.getConfig().contains(path + "." + s + ".display")) {
                    ItemStack is = plugin.getItemStack(path + "." + s + ".display", plugin.getConfig());
                    menuElement = new CustomMenuElement(is);
                } else if (plugin.getConfig().getBoolean(path + "." + s + ".spacer", false)) {
                    menuElement = new SpacerMenuElement();
                } else {
                    continue; // user = idiot
                }
                for (int i = 9; i <= repeat; i++) {
                    menuElements.put(slot + i, menuElement);
                }
            }
        }

        Collections.sort(quests);
        int slot = 0;
        for (Quest quest : quests) {
            while (menuElements.containsKey(slot)) {
                slot++;
            }
            if (config.getBoolean("options.gui-hide-locked")) {
                QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
                long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
                if (!owner.getQuestProgressFile().hasMetRequirements(quest) || (!quest.isRepeatable() && questProgress.isCompletedBefore()) || cooldown > 0) {
                    continue;
                }
            }
            if (config.getBoolean("options.gui-hide-quests-nopermission") && quest.isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getPlayerUUID()).hasPermission("quests.quest." + quest.getId())) {
                    continue;
                }
            }
            menuElements.put(slot, new QuestMenuElement(plugin, owner, quest.getId()));
            slot++;
        }

        for (Integer integer : menuElements.keySet()) {
            if (integer + 1 > maxElement) {
                maxElement = integer + 1;
            }
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
        String title = Chat.color(config.getString("options.guinames.quests-menu"));

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        ItemStack back = config.getItem("gui.back-button");
        ItemStack bg = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        Inventory inventory = Bukkit.createInventory(null, 54, title);

        int highestOnPage = 0;
        for (int pointer = pageMin; pointer < pageMax; pointer++) {
            if (menuElements.containsKey(pointer)) {
                inventory.setItem(pointer - ((page - 1) * pageSize), menuElements.get(pointer).asItemStack());
                if (pointer + 1 > highestOnPage) {
                    highestOnPage = pointer + 1;
                }
            }
        }

        pageNextLocation = -1;
        pagePrevLocation = -1;

        Map<String, String> pageplaceholders = new HashMap<>();
        pageplaceholders.put("{prevpage}", String.valueOf(page - 1));
        pageplaceholders.put("{nextpage}", String.valueOf(page + 1));
        pageplaceholders.put("{page}", String.valueOf(page));
        pageIs = replaceItemStack(config.getItem("gui.page-desc"), pageplaceholders);
        pageIs.setAmount(Math.min(page, 64));
        pagePrevIs = replaceItemStack(config.getItem("gui.page-prev"), pageplaceholders);
        pageNextIs = replaceItemStack(config.getItem("gui.page-next"), pageplaceholders);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, bg);
        }
        
        if (config.getBoolean("options.categories-enabled") && backButtonEnabled) {
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
        } else if (config.getBoolean("options.trim-gui-size") && page == 1) {
            int inventorySize = highestOnPage + (9 - highestOnPage % 9) * Math.min(1, highestOnPage % 9);
            inventorySize = inventorySize <= 0 ? 9 : inventorySize;
            if (inventorySize == 54) {
                return inventory;
            } else if (config.getBoolean("options.categories-enabled") && backButtonEnabled) {
                inventorySize += 9;
            }

            Inventory trimmedInventory = Bukkit.createInventory(null, inventorySize, title);

            for (int slot = 0; slot < trimmedInventory.getSize(); slot++) {
                if (slot >= (trimmedInventory.getSize() - 9) && backButtonEnabled) {
                    if (config.getBoolean("options.categories-enabled")) {
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
    public boolean handleClick(InventoryClickEvent event, MenuController controller) {
        //TODO maybe redo this maybe
        if (pagePrevLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage - 1);
            return true;
        } else if (pageNextLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), this, currentPage + 1);
            return true;
        } else if (config.getBoolean("options.categories-enabled") && backButtonLocation == event.getSlot()) {
            controller.openMenu(event.getWhoClicked(), superMenu, 1);
            return true;
        } else if (event.getSlot() < pageSize && menuElements.containsKey(event.getSlot() + (((currentPage) - 1) * pageSize))) {
            MenuElement menuElement = menuElements.get(event.getSlot() + ((currentPage - 1) * pageSize));
            if (menuElement instanceof QuestMenuElement) {
                QuestMenuElement questMenuElement = (QuestMenuElement) menuElement;
                Quest quest = plugin.getQuestManager().getQuestById(questMenuElement.getQuestId());
                if (event.getClick() == ClickType.LEFT) {
                    if (config.getBoolean("options.quest-autostart")) return false;
                    if (owner.startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                        QMenu currMenu = this;
                        event.getWhoClicked().closeInventory();
                        controller.openMenu(event.getWhoClicked(), currMenu, 1);
                        
                    }
                    return true;
                } else if (event.getClick() == ClickType.MIDDLE && config.getBoolean("options.quest-autostart")) {
                    MenuUtils.handleMiddleClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                    return true;
                } else if (event.getClick() == ClickType.RIGHT && config.getBoolean("options.allow-quest-cancel")
                        && owner.hasStartedQuest(quest)) {
                    MenuUtils.handleRightClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                    return true;
                }
            }
        }
        return false;
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

    public CategoryQMenu getSuperMenu() {
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
        Player player = Bukkit.getPlayer(owner.getPlayerUUID());
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                    if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                        s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    }
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
            if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            }
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }
}
