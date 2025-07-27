package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class VersionSpecificHandler16 extends VersionSpecificHandler14 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 16;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return player.getVehicle() instanceof Strider;
    }

    @Override
    public boolean isOffHandSwap(ClickType clickType) {
        return clickType == ClickType.SWAP_OFFHAND;
    }

    @Override
    public boolean isOffHandEmpty(Player player) {
        return player.getInventory().getItemInOffHand().getAmount() == 0;
    }

    @Override
    public ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral()
        };
    }
}
