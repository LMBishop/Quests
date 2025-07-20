package com.leonardobishop.quests.bukkit.item;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.context.Context;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomFishingQuestItem extends QuestItem {

    private final List<String> loots;

    public CustomFishingQuestItem(final String id, final List<String> loots) {
        super("customfishing", id);
        this.loots = List.copyOf(loots);
    }

    @Override
    public ItemStack getItemStack() {
        return BukkitCustomFishingPlugin.getInstance()
                .getItemManager()
                .buildAny(
                        Context.player(null),
                        !this.loots.isEmpty() ? this.loots.getFirst() : "null"
                );
    }

    @Override
    public boolean compareItemStack(final ItemStack other, final boolean exactMatch) {
        final String loot = BukkitCustomFishingPlugin.getInstance()
                .getItemManager()
                .getCustomFishingItemID(other);
        return loot != null && this.loots.contains(loot);
    }
}
