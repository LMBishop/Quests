package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class VersionSpecificHandler9 extends VersionSpecificHandler8 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 9;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return player.isGliding();
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

        for (ItemStack existingItemStack : inventory.getStorageContents()) {
            if (existingItemStack == null) {
                availableSpace += newItemStack.getMaxStackSize();
            }
        }

        return availableSpace;
    }

    @Override
    public boolean isHotbarMoveAndReaddSupported() {
        return false;
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot) {
        return switch (slot) {
            case CHEST -> inventory.getChestplate();
            case FEET -> inventory.getBoots();
            case HAND -> inventory.getItemInHand();
            case HEAD -> inventory.getHelmet();
            case LEGS -> inventory.getLeggings();
            case OFF_HAND -> inventory.getItemInOffHand();

            // there are 6 equipment slots on 1.9
            default -> null;
        };
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEvent event) {
        return event.getHand();
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEntityEvent event) {
        return event.getHand();
    }
}
