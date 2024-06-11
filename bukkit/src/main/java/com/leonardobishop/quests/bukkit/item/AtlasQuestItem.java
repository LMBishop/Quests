package com.leonardobishop.quests.bukkit.item;

import net.jeracraft.atlas.Atlas;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class AtlasQuestItem extends QuestItem {

    private final String atlasId;

    public AtlasQuestItem(String id, String atlasId) {
        super("atlas", id);
        this.atlasId = atlasId;
    }

    @Override
    public ItemStack getItemStack() {
        return Atlas.plugin.itemUtils.createItemStack(atlasId, 1, null);
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        String itemId = other.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Atlas.plugin, "item_id"), PersistentDataType.STRING);
        if (itemId == null) {
            return false;
        }
        return Objects.equals(itemId.toLowerCase(), atlasId.toLowerCase());
    }
}