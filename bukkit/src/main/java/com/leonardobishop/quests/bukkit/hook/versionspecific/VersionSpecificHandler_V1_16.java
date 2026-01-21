package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
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
    public @Nullable ItemStack[] getSmithItems(SmithItemEvent event) {
        return new @Nullable ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral()
        };
    }
}
