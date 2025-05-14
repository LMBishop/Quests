package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VersionSpecificHandler11 extends VersionSpecificHandler9 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 11;
    }

    @Override
    public boolean isPlayerOnDonkey(Player player) {
        return player.getVehicle() instanceof Donkey;
    }

    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof Horse;
    }

    @Override
    public boolean isPlayerOnLlama(Player player) {
        return player.getVehicle() instanceof Llama;
    }

    @Override
    public boolean isPlayerOnMule(Player player) {
        return player.getVehicle() instanceof Mule;
    }

    @Override
    public boolean isPlayerOnSkeletonHorse(Player player) {
        return player.getVehicle() instanceof SkeletonHorse;
    }

    @Override
    public boolean isPlayerOnZombieHorse(Player player) {
        return player.getVehicle() instanceof ZombieHorse;
    }

    @Override
    public int removeItem(Inventory inventory, int slot, int amountToRemove) {
        ItemStack item = inventory.getItem(slot);

        if (item == null) {
            return 0;
        }

        int amountInStack = item.getAmount();
        int newAmountInStack = Math.max(0, amountInStack - amountToRemove);
        item.setAmount(newAmountInStack);

        return amountInStack - newAmountInStack;
    }
}
