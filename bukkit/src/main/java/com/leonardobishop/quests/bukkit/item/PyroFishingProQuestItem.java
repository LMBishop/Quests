package com.leonardobishop.quests.bukkit.item;

import com.leonardobishop.quests.bukkit.util.NamespacedKeyUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class PyroFishingProQuestItem extends QuestItem {

    private static final NamespacedKey FISH_NUMBER_KEY = NamespacedKeyUtils.fromString("pyrofishingpro:fishnumber");
    private static final NamespacedKey TIER_KEY = NamespacedKeyUtils.fromString("pyrofishingpro:tier");

    // there is really no way to get the item properly
    private static final ItemStack ITEM = new ItemStack(Material.COOKED_COD, 1);

    static {
        final ItemMeta meta = PyroFishingProQuestItem.ITEM.getItemMeta();
        //noinspection deprecation
        meta.setDisplayName("This item type cannot be gotten");
        PyroFishingProQuestItem.ITEM.setItemMeta(meta);
    }

    private final int fishNumber;
    private final String tier;

    public PyroFishingProQuestItem(final @NotNull String id, final @Range(from = -1, to = Integer.MAX_VALUE) int fishNumber, final @Nullable String tier) {
        super("pyrofishingpro", id);
        this.fishNumber = fishNumber;
        this.tier = tier;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return PyroFishingProQuestItem.ITEM;
    }

    @Override
    public boolean compareItemStack(final @NotNull ItemStack other, final boolean exactMatch) {
        final ItemMeta meta = other.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();

        final int fishNumber = pdc.getOrDefault(PyroFishingProQuestItem.FISH_NUMBER_KEY, PersistentDataType.INTEGER, -1);
        if (fishNumber == -1) {
            return false;
        }

        final String tier = pdc.get(PyroFishingProQuestItem.TIER_KEY, PersistentDataType.STRING);
        if (tier == null) {
            return false;
        }

        return (this.fishNumber == -1 || this.fishNumber == fishNumber)
                && (this.tier == null || this.tier.equals(tier));
    }
}
