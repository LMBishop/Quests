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
        if (item == null) {
            return null;
        }
        return item.getItem();
    }

    @Override
    public boolean compareItemStack(ItemStack other) {
        SlimefunItem item = SlimefunItem.getByItem(other);

        if (item == null) return false;

        String id = item.getId();
        return slimefunId.equals(id);
    }

}
