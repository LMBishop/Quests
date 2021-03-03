package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Menu list of categories.
 */
public class QMenuCategory implements QMenu {

    private final Quests plugin;
    private final int pageSize = 45;
    private final HashMap<Integer, QMenuQuest> slotsToMenuQuest = new HashMap<>();
    private final QPlayer owner;

    public QMenuCategory(Quests plugin, QPlayer owner) {
        this.plugin = plugin;
        this.owner = owner;
    }

    public void populate(List<QMenuQuest> menuQuests) {
        int slot = 0;
        for (QMenuQuest qMenuQuest : menuQuests) {
            if (Options.GUI_HIDE_CATEGORIES_NOPERMISSION.getBooleanValue() && Quests.get().getQuestManager().getCategoryById(qMenuQuest.getCategoryName()).isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getUuid()).hasPermission("quests.category." + qMenuQuest.getCategoryName())) {
                    continue;
                }
            }
            slotsToMenuQuest.put(slot, qMenuQuest);
            slot++;
        }
    }

    @Override
    public HashMap<Integer, QMenuQuest> getSlotsToMenu() {
        return slotsToMenuQuest;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public Inventory toInventory(int page) {
        int pageMin = pageSize * (page - 1);
        int pageMax = pageSize * page;
        String title = Options.color(Options.GUITITLE_QUESTS_CATEGORY.getStringValue());

        ItemStack pageIs = new ItemStack(Material.DIRT);

        Inventory inventory = Bukkit.createInventory(null, 54, title);

        for (int pointer = pageMin; pointer < pageMax; pointer++) {
            if (slotsToMenuQuest.containsKey(pointer)) {
                Category category = Quests.get().getQuestManager().getCategoryById(slotsToMenuQuest.get(pointer).getCategoryName());
                if (category != null) {
                    inventory.setItem(pointer, replaceItemStack(category.getDisplayItem()));
                }
            }
        }

        inventory.setItem(49, replaceItemStack(pageIs));

        if (Options.TRIM_GUI_SIZE.getBooleanValue() && page == 1) {
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

            for (int slot = 0; slot < pageMax; slot++) {
                if (slot >= trimmedInventory.getSize()) {
                    break;
                }
                trimmedInventory.setItem(slot, inventory.getItem(slot));
            }
            return trimmedInventory;
        } else {
            return inventory;
        }

    }

    @Override
    public void handleClick(InventoryClickEvent event, MenuController controller) {
        if (this.getSlotsToMenu().containsKey(event.getSlot())) {
            QMenuQuest qMenuQuest = this.getSlotsToMenu().get(event.getSlot());
            controller.getBuffer().add(event.getWhoClicked().getUniqueId());
            if (this.getOwner().openCategory(plugin.getQuestManager().getCategoryById(qMenuQuest.getCategoryName()), qMenuQuest) != 0) {
                controller.getBuffer().remove(event.getWhoClicked().getUniqueId());
                event.getWhoClicked().sendMessage(Messages.QUEST_CATEGORY_PERMISSION.getMessage());
            }
        }
    }

    public ItemStack replaceItemStack(ItemStack is) {
        if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
            ItemStack newItemStack = is.clone();
            List<String> lore = newItemStack.getItemMeta().getLore();
            List<String> newLore = new ArrayList<>();
            ItemMeta ism = newItemStack.getItemMeta();
            Player player = Bukkit.getPlayer(owner.getUuid());
            ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            if (lore != null) {
                for (String s : lore) {
                    s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    newLore.add(s);
                }
            }
            ism.setLore(newLore);
            newItemStack.setItemMeta(ism);
            return newItemStack;
        }
        return is;
    }

}
