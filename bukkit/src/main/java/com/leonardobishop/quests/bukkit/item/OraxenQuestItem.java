package com.leonardobishop.quests.bukkit.item;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenQuestItem extends QuestItem {

    private final String oraxenId;

    public OraxenQuestItem(String id, String oraxenId) {
        super("oraxen", id);
        this.oraxenId = oraxenId;
    }

    @Override
    public ItemStack getItemStack() {
        return OraxenItems.getItemById(this.oraxenId).build();
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        final String otherId = OraxenItems.getIdByItem(other);
        return this.oraxenId.equals(otherId);
    }
}
