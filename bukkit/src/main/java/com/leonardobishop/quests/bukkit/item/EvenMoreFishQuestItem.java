package com.leonardobishop.quests.bukkit.item;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.fishing.items.Fish;
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
        final Fish fish = EvenMoreFish.getInstance()
                .getApi()
                .getFish(this.rarityName, this.fishName);
        return fish != null ? fish.give() : null;
    }

    @Override
    public boolean compareItemStack(final ItemStack other, final boolean exactMatch) {
        final Fish fish = EvenMoreFish.getInstance()
                .getApi()
                .getFish(other);
        return fish != null
                && fish.getRarity().getId().equals(this.rarityName)
                && fish.getName().equals(this.fishName);
    }
}
