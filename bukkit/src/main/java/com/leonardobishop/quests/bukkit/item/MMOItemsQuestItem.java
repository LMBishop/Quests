package com.leonardobishop.quests.bukkit.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class MMOItemsQuestItem extends QuestItem {

    private final String mmoItemType;
    private final String mmoItemId;

    public MMOItemsQuestItem(String id, String mmoItemType, String mmoItemId) {
        super("mmoitems", id);
        this.mmoItemType = mmoItemType;
        this.mmoItemId = mmoItemId;
    }

    @Override
    public ItemStack getItemStack() {
        final Type type = Type.get(mmoItemType);
        return MMOItems.plugin.getItem(type, mmoItemId);
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        NBTItem item = NBTItem.get(other);
        if (!item.hasType()) {
            return false;
        }

        String type = item.getType();
        String id = item.getString("MMOITEMS_ITEM_ID");
        return mmoItemType.equals(type) && mmoItemId.equals(id);
    }
}
