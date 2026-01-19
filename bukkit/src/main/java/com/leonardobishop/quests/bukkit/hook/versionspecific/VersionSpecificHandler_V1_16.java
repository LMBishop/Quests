package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class VersionSpecificHandler_V1_16 extends VersionSpecificHandler_V1_15_2 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_16;
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
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral()
        };
    }
}
