package com.leonardobishop.quests.bukkit.item;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class SlimefunQuestItem extends QuestItem {

    private final String slimefunId;

    public SlimefunQuestItem(String id, String slimefunId) {
        super("slimefun", id);
        this.slimefunId = slimefunId;
    }

    @Override
    public ItemStack getItemStack() {
        return SlimefunItem.getByID(slimefunId).getItem();
    }

    @Override
    public boolean compareItemStack(ItemStack other) {
        SlimefunItem item = SlimefunItem.getByItem(other);

        if (item == null) return false;

        String id = item.getId();
        return slimefunId.equals(id);
    }

}
