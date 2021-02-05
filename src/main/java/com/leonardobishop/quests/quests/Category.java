package com.leonardobishop.quests.quests;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final String id;
    private final ItemStack displayItem;
    private final boolean permissionRequired;
    private final List<String> registeredQuestIds = new ArrayList<>();

    public Category(String id, ItemStack displayItem, boolean permissionRequired) {
        this.id = id;
        this.displayItem = displayItem;
        this.permissionRequired = permissionRequired;
    }

    public String getId() {
        return id;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void registerQuestId(String questid) {
        registeredQuestIds.add(questid);
    }

    public List<String> getRegisteredQuestIds() {
        return registeredQuestIds;
    }


    public String getDisplayNameStripped() {
        return ChatColor.stripColor(this.displayItem.getItemMeta().getDisplayName());
    }
}
