package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class VersionSpecificHandler8 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 8;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return false;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return false;
    }

    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof Horse;
    }

    @Override
    public boolean isOffHandSwap(ClickType clickType) {
        return false;
    }

    @Override
    public boolean isOffHandEmpty(Player player) {
        return false;
    }

    @Override
    public int getAvailableSpace(Player player, ItemStack newItemStack) {
        int availableSpace = 0;
        PlayerInventory inventory = player.getInventory();
        HashMap<Integer, ? extends ItemStack> itemStacksWithSameMaterial = inventory.all(newItemStack.getType());
        for (ItemStack existingItemStack : itemStacksWithSameMaterial.values()) {
            if (newItemStack.isSimilar(existingItemStack)) {
                availableSpace += (newItemStack.getMaxStackSize() - existingItemStack.getAmount());
            }
        }

        for (ItemStack existingItemStack : inventory.getContents()) {
            if (existingItemStack == null) {
                availableSpace += newItemStack.getMaxStackSize();
            }
        }

        return availableSpace;
    }

    @Override
    public boolean isHotbarMoveAndReaddSupported() {
        return true;
    }

    @Override
    public boolean isCaveVinesPlantWithBerries(BlockData blockData) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getItemInHand();
    }
}
