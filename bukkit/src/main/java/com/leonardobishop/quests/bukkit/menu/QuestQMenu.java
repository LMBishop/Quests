package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.CustomMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.QuestMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.SpacerMenuElement;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.StringUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
 * Represents a menu for a specified category (or all if they are disabled),
 * which contains a listing of different quests.
 */
public class QuestQMenu implements QMenu {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final Int2ObjectOpenHashMap<MenuElement> menuElements = new Int2ObjectOpenHashMap<>();
    private final CategoryQMenu superMenu;
    private final String categoryName;
    private final int pageSize = 45;
    private final QPlayer owner;
    private final ClickType startClickType;
    private final ClickType trackClickType;
    private final ClickType cancelClickType;

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

        this.startClickType = MenuUtils.getClickType(config, "options.gui-actions.start-quest", "LEFT");
        this.trackClickType = MenuUtils.getClickType(config, "options.gui-actions.track-quest", "MIDDLE");
        this.cancelClickType = MenuUtils.getClickType(config, "options.gui-actions.cancel-quest", "RIGHT");
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
                if (!StringUtils.isNumeric(s)) continue;
                int slot = Integer.parseInt(s);
                int repeat = plugin.getConfig().getInt(path + "." + s + ".repeat");
                MenuElement menuElement;
                if (plugin.getConfig().contains(path + "." + s + ".display")) {
                    ItemStack is = plugin.getConfiguredItemStack(path + "." + s + ".display", plugin.getConfig());
                    List<String> commands = plugin.getQuestsConfig().getStringList(path + "." + s + ".commands");
                    menuElement = new CustomMenuElement(plugin, owner.getPlayerUUID(), is, commands);
                } else if (plugin.getConfig().getBoolean(path + "." + s + ".spacer", false)) {
                    menuElement = new SpacerMenuElement();
                } else continue; // user = idiot

                for (int i = 0; i <= repeat; i++) {
                    menuElements.put(slot + i, menuElement);
                }
            }
        }

        Collections.sort(quests);
        int slot = 0;
        for (Quest quest : quests) {
            while (menuElements.containsKey(slot)) slot++;
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
            menuElements.put(slot, new QuestMenuElement(plugin, owner, quest));
            slot++;
        }

        maxElement = menuElements.size() > 0 ? Collections.max(menuElements.keySet()) + 1 : 0;
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
        String title = Chat.legacyColor(config.getString("options.guinames.quests-menu"));

        ItemStack pageIs;
        ItemStack pagePrevIs;
        ItemStack pageNextIs;
        ItemStack back = config.getItem("gui.back-button");

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
                if (slot >= (trimmedInventory.getSize() - 9) && backButtonEnabled){
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
            if (menuElement instanceof QuestMenuElement questMenuElement) {
                Quest quest = questMenuElement.getQuest();
                if (!owner.hasStartedQuest(quest) && event.getClick() == startClickType) {
                    if (config.getBoolean("option.gui-close-after-accept", true)) {
                        if (owner.startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                    return true;
                } else if (event.getClick() == trackClickType) {
                    MenuUtils.handleMiddleClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                    return true;
                } else if (event.getClick() == cancelClickType) {
                    MenuUtils.handleRightClick(plugin, this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
                    return true;
                }
            } else if (menuElement instanceof CustomMenuElement customMenuElement) {
                for (String command : customMenuElement.getCommands()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            command.replace("{player}", event.getWhoClicked().getName()));
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

}
