package me.fatpigsarefat.quests.obj.misc;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.obj.Options;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.quests.Category;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class QMenuCategory implements QMenu {

    private final int pageSize = 45;
    private HashMap<Integer, QMenuQuest> slotsToMenuQuest = new HashMap<>();
    private QPlayer owner;

    public QMenuCategory(QPlayer owner) {
        this.owner = owner;
    }

    public void populate(List<QMenuQuest> menuQuests) {
        int slot = 0;
        for (QMenuQuest qMenuQuest : menuQuests) {
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
                Category category = Quests.getQuestManager().getCategoryById(slotsToMenuQuest.get(pointer).getCategoryName());
                if (category != null) {
                    inventory.setItem(pointer, category.getDisplayItem());
                }
            }
        }

        inventory.setItem(49, pageIs);

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
                if (slot >= trimmedInventory.getSize()){
                    break;
                }
                trimmedInventory.setItem(slot, inventory.getItem(slot));
            }
            return trimmedInventory;
        } else {
            return inventory;
        }

    }

}
