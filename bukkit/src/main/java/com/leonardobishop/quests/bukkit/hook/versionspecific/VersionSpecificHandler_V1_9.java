package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;

public class VersionSpecificHandler_V1_9 extends VersionSpecificHandler_V1_8 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_9;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return player.isGliding();
    }

    @Override
    public boolean isOffHandEmpty(Player player) {
        return player.getInventory().getItemInOffHand().getAmount() == 0;
    }

    @Override
    public @Nullable ItemStack[] getStorageContents(PlayerInventory inventory) {
        return inventory.getStorageContents();
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
