package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VersionSpecificHandler_V1_15_2 extends VersionSpecificHandler_V1_14 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_15_2;
    }

    @Override
    public ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot) {
        return inventory.getItem(slot);
    }
}
