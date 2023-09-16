package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class VersionSpecificHandler16 extends VersionSpecificHandler11 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 16;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return player.getVehicle() != null && player.getVehicle().getType() == EntityType.STRIDER;
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
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral()
        };
    }
}
