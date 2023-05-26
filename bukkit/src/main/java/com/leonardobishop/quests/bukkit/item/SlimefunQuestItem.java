package com.leonardobishop.quests.bukkit.item;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.inventory.ItemStack;

public class SlimefunQuestItem extends QuestItem {

    private final String slimefunId;

    public SlimefunQuestItem(String id, String slimefunId) {
        super("slimefun", id);
        this.slimefunId = slimefunId;
    }

    @Override
    public ItemStack getItemStack() {
        SlimefunItem item = SlimefunItem.getById(slimefunId);
        return item != null ? item.getItem() : null;
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        SlimefunItem item = SlimefunItem.getByItem(other);
        return item != null && slimefunId.equals(item.getId());
    }
}
