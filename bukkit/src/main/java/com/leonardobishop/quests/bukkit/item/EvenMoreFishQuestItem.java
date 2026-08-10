package com.leonardobishop.quests.bukkit.item;

import com.oheers.fish.api.fishing.items.AbstractFishManager;
import com.oheers.fish.api.fishing.items.IFish;
import org.bukkit.inventory.ItemStack;

public class EvenMoreFishQuestItem extends QuestItem {

    private final String rarityName;
    private final String fishName;

    public EvenMoreFishQuestItem(final String id, final String rarityName, final String fishName) {
        super("evenmorefish", id);
        this.rarityName = rarityName;
        this.fishName = fishName;
    }

    @Override
    public ItemStack getItemStack() {
        final IFish fish = AbstractFishManager.getInstance().getFish(this.rarityName, this.fishName);
        return fish != null ? fish.give() : null;
    }

    @Override
    public boolean compareItemStack(final ItemStack other, final boolean exactMatch) {
        final IFish fish = AbstractFishManager.getInstance().getFish(other);
        return fish != null
                && fish.getRarity().getId().equals(this.rarityName)
                && fish.getName().equals(this.fishName);
    }
}
