package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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
    public boolean isPlayerOnCamel(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnDonkey(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.DONKEY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.HORSE;
    }

    @Override
    public boolean isPlayerOnLlama(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnMule(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.MULE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnSkeletonHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.SKELETON_HORSE;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnZombieHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.UNDEAD_HORSE;
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

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot) {
        return switch (slot) {
            case CHEST -> inventory.getChestplate();
            case FEET -> inventory.getBoots();
            case HAND -> inventory.getItemInHand();
            case HEAD -> inventory.getHelmet();
            case LEGS -> inventory.getLeggings();

            // there are 5 equipment slots on 1.8
            default -> null;
        };
    }

    @Override
    public ItemStack getItem(PlayerBucketEmptyEvent event) {
        return new ItemStack(event.getBucket(), 1);
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEvent event) {
        return EquipmentSlot.HAND;
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEntityEvent event) {
        return EquipmentSlot.HAND;
    }

    @Override
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[0];
    }

    @Override
    public String getSmithMode(SmithItemEvent event) {
        return null;
    }

    @Override
    public boolean isGoat(Entity entity) {
        return false;
    }
}
