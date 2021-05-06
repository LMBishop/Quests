package com.leonardobishop.quests.obj.misc.creator;

import com.leonardobishop.quests.obj.misc.QMenu;
import com.leonardobishop.quests.player.QPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class QMenuCreator implements QMenu {

    private final QPlayer owner;

    public QMenuCreator(QPlayer owner) {
        this.owner = owner;
    }

    @Override
    public Map<Integer, String> getSlotsToMenu() {
        return null;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public Inventory toInventory(int page) {
        String title = "Quest Creator";

//        Inventory inventory = Bukkit.createInventory(null, 9, title);
//
//        ItemStack newQuest = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
//        ItemMeta newQuestM = newQuest.getItemMeta();
//        List<String> newQuestL = new ArrayList<>();
//        newQuestM.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "New Quest");
//        newQuestL.add(ChatColor.GRAY + "Click to make a new quest.");
//        newQuestM.setLore(newQuestL);
//        newQuest.setItemMeta(newQuestM);
//
//        ItemStack editQuest = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
//        ItemMeta editQuestM = editQuest.getItemMeta();
//        List<String> editQuestL = new ArrayList<>();
//        editQuestM.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Edit Quest");
//        editQuestL.add(ChatColor.GRAY + "Click to edit an existing quest.");
//        editQuestM.setLore(editQuestL);
//        editQuest.setItemMeta(editQuestM);
//
//        ItemStack removeQuest = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
//        ItemMeta removeQuestM = removeQuest.getItemMeta();
//        List<String> removeQuestL = new ArrayList<>();
//        removeQuestM.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Delete Quest");
//        removeQuestL.add(ChatColor.GRAY + "Click to delete an existing quest.");
//        removeQuestM.setLore(removeQuestL);
//        removeQuest.setItemMeta(removeQuestM);
//
//        inventory.setItem(2, newQuest);
//        inventory.setItem(4, editQuest);
//        inventory.setItem(6, removeQuest);
//        return inventory;

        return Bukkit.createInventory(null, 9, title);
    }

}
